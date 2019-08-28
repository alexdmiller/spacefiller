package algoplex2.scenes;

import spacefiller.graph.*;
import processing.core.PConstants;
import processing.core.PVector;
import spacefiller.mapping.Quad;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;

public class TriangleScene extends GridScene {
  private float t = 0;

  @Mod(min = 0, max = 20)
  public float shiftAmount = 0;

  @Mod(min = 0, max = 5)
  public float yMod = 0;

  @Mod(min = 0, max = 5)
  public float xMod = 0;

  @Mod(min = 0, max = 10)
  public float triangleMod = 0;

  @Mod(min = 0, max = 10)
  public float lineMod = 0f;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  @Mod(min = 0, max = 1)
  public float mix = 1;

  private PerlinNoise perlin;

  public TriangleScene() {
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.blendMode(PConstants.ADD);

    t += speed;

    float shift = t + shiftAmount;

    graphics.noStroke();
    graphics.noFill();
    int quadIndex = 0;
    for (Quad quad : grid.getSquares()) {
      float row = quadIndex / grid.getColumns() - grid.getRows() / 2f;
      float col = quadIndex % grid.getColumns() - grid.getColumns() / 2f;

      int triangleIndex = 0;
      for (Node[] triangle : quad.getTriangles()) {
        float v = (float) ((Math.sin(
            row * yMod + col * xMod +
            triangleIndex / 4f * Math.PI * 2 * triangleMod
            + shift) + 1) / 2);

        graphics.fill((float) Math.pow(v, 5) * 255 * mix);

        graphics.triangle(
            triangle[0].position.x, triangle[0].position.y,
            triangle[1].position.x, triangle[1].position.y,
            triangle[2].position.x, triangle[2].position.y);

        for (int i = 0; i < triangle.length; i++) {
          float lv = (float) ((Math.sin(
                  row * yMod + col * xMod +
                  triangleIndex / 4f * Math.PI * 2 * triangleMod +
                  i / 3f * Math.PI * lineMod
                  + shift) + 1) / 2);
          graphics.stroke((float) (Math.pow(lv, 10) *255) * (1 - mix));
          graphics.strokeWeight(5);

          PVector p1 = triangle[i].position;
          PVector p2 = triangle[(i + 1) % triangle.length].position;
          graphics.line(p1.x, p1.y, p2.x, p2.y);
        }

        triangleIndex++;
      }

      quadIndex++;
    }

    graphics.blendMode(PConstants.BLEND);

    super.draw(graphics);
  }

}
