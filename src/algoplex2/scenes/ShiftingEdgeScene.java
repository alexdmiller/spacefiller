package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import processing.core.PConstants;
import processing.core.PVector;
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

  public float jitter = 0;

  private PerlinNoise perlin;
  private PerlinNoise jitterPerlin;

  public ShiftingEdgeScene() {
    perlin = new PerlinNoise();
    jitterPerlin = new PerlinNoise();
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
              + (float) quadIndex * quadMod
              + (float) triangleIndex * triangleMod
              + (float) i * lineMod) + 1) / 2;

          graphics.stroke((float) (Math.pow(v, 10) *255));
          graphics.strokeWeight(5);

          PVector p1 = transform(triangle[i].position);
          PVector p2 = transform(triangle[(i + 1) % triangle.length].position);
          graphics.line(p1.x, p1.y, p2.x, p2.y);
        }
        triangleIndex++;
      }

      quadIndex++;
    }

    super.draw(graphics);
    graphics.blendMode(PConstants.BLEND);
  }

  private PVector transform(PVector p) {
    return new PVector(p.x + (jitterPerlin.noise(p.x, p.y, t) - 0.5f) * jitter,
        p.y + (jitterPerlin.noise(p.x, p.y, t) - 0.5f) * jitter);
  }
}
