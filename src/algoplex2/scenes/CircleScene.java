package algoplex2.scenes;

import processing.core.PGraphics;
import spacefiller.mapping.Quad;
import spacefiller.remote.Mod;
import toxi.math.noise.PerlinNoise;

/**
 * Created by miller on 10/3/17.
 */
public class CircleScene extends GridScene {
  private float t;
  public float speed = 0.1f;

  @Mod(min = (float) Math.PI / 4, max = (float) (4 * Math.PI))
  public float length = (float) Math.PI;

  public float delta = 0.1f;

  @Mod(min = 0, max = 30)
  public float spread = 10;

  private PerlinNoise perlin;

  @Mod(min = 0, max = 1)
  public float bigAmp = 0.5f;

  @Mod(min = 0, max = 1)
  public float bigFreq = 0.5f;

  @Mod(min = 0, max = 1)
  public float smallAmp = 0.5f;

  @Mod(min = 0, max = 1)
  public float smallFreq = 0.5f;

  @Mod(min = 0, max = 50)
  public float noiseAmp = 0.5f;

  @Mod(min = 0, max = 1)
  public float shiftAmount = 0.2f;

  public CircleScene() {
    super();
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;
    float i = 0;

    graphics.stroke(255);
    graphics.noFill();
    for (Quad quad : grid.getSquares()) {
      graphics.strokeWeight(3);
      i += Math.PI / 10;

      graphics.pushMatrix();
      graphics.translate(quad.getCenter().position.x, quad.getCenter().position.y);
      graphics.beginShape();
      for (float step = 0; step < length; step += delta) {
        float value = f(step + t, i, grid.getCellSize() / 4);
        graphics.vertex(
            (float) Math.cos(step + t) * value,
            (float) Math.sin(step + t) * value
        );
      }
      graphics.endShape();

      graphics.beginShape();
      for (float step = 0; step < length; step += delta) {
        float value = f(step + t, i, grid.getCellSize() / 4 + spread);
        graphics.vertex(
            (float) Math.cos(step + t) * value,
            (float) Math.sin(step + t) * value
        );
      }
      graphics.endShape();

      graphics.strokeWeight(1);
      for (float step = 0; step < length; step += delta) {
        float v1 = f(step + t, i, grid.getCellSize() / 4);
        float v2 = f(step + t, i, grid.getCellSize() / 4 + spread);
        graphics.line(
            (float) Math.cos(step + t) * v1,
            (float) Math.sin(step + t) * v1,
            (float) Math.cos(step + t) * v2,
            (float) Math.sin(step + t) * v2
        );
      }

      graphics.popMatrix();
    }
  }

  private float f(float t, float offset, float max) {
    return (float) (
        Math.sin(t * bigFreq + offset * shiftAmount) * bigAmp * max
        + Math.sin(t * smallFreq * 10 + offset * shiftAmount) * smallAmp * max / 5)
        + perlin.noise(t) * noiseAmp;
  }
}
