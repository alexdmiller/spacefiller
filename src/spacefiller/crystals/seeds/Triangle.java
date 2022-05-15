package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.patchbay.annotations.Midi;
import spacefiller.patchbay.annotations.Scale;

import java.util.List;

public class Triangle extends Animator.SimpleAnimator {
  @Midi(channel = 0, control = 2)
  @Scale(min = 1, max = 100)
  public float size = 20;

  @Midi(channel = 0, control = 3)
  @Scale(min = -6.283185307179586f, max = 6.283185307179586f)
  public float angle;

  public Triangle(int size) {
    this.size = size;
  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight, int size) {
    graphics.stroke(255);
    graphics.noFill();
    graphics.strokeWeight(strokeWeight);

    graphics.pushMatrix();
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.rotate(angle);
    graphics.beginShape();
    graphics.vertex(
        (float) Math.cos(0) * size,
        (float) Math.sin(0) * size);

    graphics.vertex(
        (float) Math.cos(Math.PI * 2 / 3) * size,
        (float) Math.sin(Math.PI * 2 / 3) * size);
    graphics.vertex(
        (float) Math.cos(2 * Math.PI * 2 / 3) * size,
        (float) Math.sin(2 * Math.PI * 2 / 3) * size);
    graphics.endShape(PConstants.CLOSE);
    graphics.popMatrix();
//    graphics.line(middleX-size, middleY-size, middleX, middleY);
//    graphics.line(middleX-size, middleY-size, middleX, middleY);
//    graphics.line(middleX-size, middleY+size, middleX, middleY);
//    graphics.line(middleX+size, middleY+size, middleX, middleY);
  }

  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    doDraw(graphics, frameCount, 1, (int) size);
  }

  @Override
  public void preview(PGraphics graphics) {
    doDraw(graphics, 60, 20, 200);
  }
}
