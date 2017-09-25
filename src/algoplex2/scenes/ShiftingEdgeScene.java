package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import processing.core.PConstants;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;

public class ShiftingEdgeScene extends GridScene {
  private float t = 0;

  @Mod(min = 0, max = 20)
  public float shiftAmount = 0;

  @Mod(min = 0, max = 6.28318530f)
  public float triangleMod = 0;

  @Mod(min = 0, max = 6.28318530f)
  public float quadMod = 0;

  @Mod(min = 0, max = 6.28318530f)
  public float lineMod = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private PerlinNoise perlin;

  public ShiftingEdgeScene() {
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
      int triangleIndex = 0;
      for (Node[] triangle : quad.getTriangles()) {
        for (int i = 0; i < triangle.length; i++) {

          float v = (float) (Math.sin(shift
              + quadIndex * quadMod
              + triangleIndex * triangleMod
              + i * lineMod) + 1) / 2;

          graphics.stroke((float) (Math.pow(v, 10) *255));
          graphics.strokeWeight(5);
          graphics.line(
              triangle[i].position.x,
              triangle[i].position.y,
              triangle[(i + 1) % triangle.length].position.x,
              triangle[(i + 1) % triangle.length].position.y);
        }
        triangleIndex++;
      }

      quadIndex++;
    }

    super.draw(graphics);
    graphics.blendMode(PConstants.BLEND);
  }
}
