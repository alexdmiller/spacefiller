package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;

public class PerlinTriangles extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private PerlinNoise perlin;

  private PGraphics noiseRender;
  private PGraphics pattern;

  public PerlinTriangles() {
    perlin = new PerlinNoise();
  }

  public void setup() {
    noiseRender =
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    graphics.noStroke();
    graphics.fill(255);
    for (Node[] triangle : grid.getTriangles()) {
      if (perlin.noise(triangle[0].position.x / 500, triangle[0].position.y / 500, t) > 0.5f
          && perlin.noise(triangle[1].position.x / 500, triangle[1].position.y / 500, t) > 0.5f
          && perlin.noise(triangle[2].position.x / 500, triangle[2].position.y / 500, t) > 0.5f) {
        graphics.triangle(
            triangle[0].position.x, triangle[0].position.y,
            triangle[1].position.x, triangle[1].position.y,
            triangle[2].position.x, triangle[2].position.y);
      }
    }

    super.draw(graphics);
  }

}
