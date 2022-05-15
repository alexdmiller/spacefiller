package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class CircleZoomLines extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight) {
    graphics.ellipseMode(PConstants.CENTER);
    graphics.noFill();

    for (int i = 6; i > 0; i--) {
      graphics.stroke(255);
      int size = (int) ((float) frameCount / 2 + i * graphics.width / 3) % (graphics.width * 2);
      graphics.strokeWeight(strokeWeight);
      graphics.ellipse(graphics.width / 2, graphics.height / 2, size, size);
    }
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
