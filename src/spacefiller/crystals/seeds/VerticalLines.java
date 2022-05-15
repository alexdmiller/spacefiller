package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class VerticalLines extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight) {
    for (int i = 0; i < graphics.width; i += 20) {
      int x = (i + frameCount / 5) % graphics.width;
      graphics.stroke(255);
      graphics.strokeWeight(strokeWeight);
      graphics.line(x, 0, x, graphics.height);
    }
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    doDraw(graphics, frameCount, 1);
  }

  @Override
  public void preview(PGraphics graphics) {
    doDraw(graphics, 60, 10);
  }
}
