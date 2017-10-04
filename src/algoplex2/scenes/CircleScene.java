package algoplex2.scenes;

import algoplex2.Quad;
import processing.core.PGraphics;

/**
 * Created by miller on 10/3/17.
 */
public class CircleScene extends GridScene {
  private float t;
  public float speed = 0.1f;

  public float length = (float) (4 * Math.PI);
  public float delta = 0.1f;

  @Override
  public void draw(PGraphics graphics) {
    t += speed;
    float i = 0;
    graphics.strokeWeight(5);
    graphics.stroke(255);
    graphics.noFill();
    for (Quad quad : grid.getSquares()) {
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
      graphics.popMatrix();
    }
  }

  private float f(float t, float offset, float max) {
    return (float) (
        Math.sin(t / 10f + offset) * max
        + Math.sin(t / 5f + offset) * max);
  }
}
