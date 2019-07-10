package algoplex2.scenes;

import algoplex2.Algoplex2;
import spacefiller.graph.*;
import spacefiller.mapping.Quad;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.math.noise.PerlinNoise;

import static processing.core.PConstants.BLEND;

public class PerlinTriangles extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.002f;

  private PerlinNoise perlin;

  private PGraphics noiseRender;
  private PGraphics pattern1;
  private PGraphics pattern2;

  @Mod(min = 0.5f, max = 0.6f)
  public float threshold = 0.5f;

  @Mod(min = 0.0005f, max = 0.002f)
  public float scale1 = 0.001f;

  @Mod(min = 0.0005f, max = 0.002f)
  public float scale2 = 0.001f;

  @Mod(min = 0, max = 6.2831853f)
  public float color1Rotation;

  @Mod(min = 0, max = 6.2831853f/2)
  public float color2Rotation = 1f;

  @Mod(min = 0, max = 10)
  public float offset;

  public float lineResolution = 10;
  public float dotResolution = 10;


  public PerlinTriangles() {
    perlin = new PerlinNoise();
  }

  public void setup() {
    noiseRender = Algoplex2.instance.createGraphics((int) grid.getWidth(), (int) grid.getHeight());
    pattern1 = Algoplex2.instance.createGraphics((int) grid.getWidth(), (int) grid.getHeight());
    pattern2 = Algoplex2.instance.createGraphics((int) grid.getWidth(), (int) grid.getHeight());
  }

  @Override
  public void draw(PGraphics graphics) {
    ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(color1Rotation);
    TColor color2 = color1.getRotatedRYB(color2Rotation);

    t += speed;

    drawNoise(scale1, offset);

    pattern1.beginDraw();
    pattern1.clear();
    pattern1.stroke(255);
    for (float y = 0; y < grid.getHeight(); y += grid.getCellSize() / lineResolution) {
      pattern1.line(0, (y + t * 100) % grid.getHeight(), grid.getWidth(), (y + t * 100) % grid.getHeight());
    }
    //pattern1.background(color1.toARGB());
    pattern1.mask(noiseRender);
    pattern1.endDraw();

    graphics.image(pattern1, 0, 0);

    pattern2.beginDraw();
    pattern2.clear();
    drawNoise(scale2, offset + 100);
    pattern2.stroke(255);
    pattern2.strokeWeight(3);
    for (float x = 0; x < grid.getWidth(); x += grid.getCellSize() / dotResolution) {
      for (float y = 0; y < grid.getHeight(); y += grid.getCellSize() / dotResolution) {
        pattern2.point(x,(y + t * 100) % grid.getHeight());
      }
    }
    //pattern2.background(color2.toARGB());
    pattern2.mask(noiseRender);
    pattern2.endDraw();
    graphics.image(pattern2, 0, 0);
    graphics.blendMode(BLEND);
    super.draw(graphics);
  }

  private void drawNoise(float scale, float offset) {
    noiseRender.beginDraw();
    noiseRender.clear();
    noiseRender.stroke(255);
    noiseRender.fill(255);
    for (Quad quad : grid.getSquares()) {
      for (Node[] triangle : quad.getTriangles()) {
        float sum = perlin.noise(triangle[0].position.x * scale, triangle[0].position.y * scale, t + offset)
          + perlin.noise(triangle[1].position.x * scale, triangle[1].position.y * scale, t + offset)
          + perlin.noise(triangle[2].position.x * scale, triangle[2].position.y * scale, t + offset);

        sum /= 3f;

        if (sum > threshold) {
          noiseRender.strokeWeight(5);
          float brightness = (float) Math.pow((sum - threshold) / threshold, 0.2f) * 255;
          noiseRender.stroke(brightness + 50);
          noiseRender.fill(brightness);
          noiseRender.triangle(
              triangle[0].position.x, triangle[0].position.y,
              triangle[1].position.x, triangle[1].position.y,
              triangle[2].position.x, triangle[2].position.y);
        }
      }
    }
    noiseRender.endDraw();
  }
}
