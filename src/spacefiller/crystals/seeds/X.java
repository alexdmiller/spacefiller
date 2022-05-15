package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;
import spacefiller.patchbay.annotations.Midi;
import spacefiller.patchbay.annotations.Scale;

import java.util.List;

public class X extends Animator.SimpleAnimator {
  private int size;

  @Midi(channel = 0, control = 1)
  @Scale(min = -6.283185307179586f, max = 6.283185307179586f)
  public float angle;

  public X(int size) {
    this.size = size;
  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight) {
    graphics.stroke(255);
    graphics.noFill();
    graphics.strokeWeight(strokeWeight);

    graphics.pushMatrix();
    graphics.translate(graphics.width / 2, graphics.height / 2);

    graphics.rotate(angle);
    for (int i = 0; i < 4; i++) {
      graphics.rotate((float) (Math.PI / 2f));
      graphics.line(0, 0, size, 0);
    }

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
    doDraw(graphics, frameCount, 1);
  }

  @Override
  public void preview(PGraphics graphics) {
    doDraw(graphics, 60, 20);
  }
}
