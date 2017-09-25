package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
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

  @Mod(min = 0, max = 5)
  public float triangleMod = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private PerlinNoise perlin;

  public TriangleScene() {
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
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

        graphics.fill(v * v * 255);

        graphics.triangle(
            triangle[0].position.x, triangle[0].position.y,
            triangle[1].position.x, triangle[1].position.y,
            triangle[2].position.x, triangle[2].position.y);

        triangleIndex++;
      }

      quadIndex++;
    }

    super.draw(graphics);
  }

}
