package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class TinySeed extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  private void doDraw(PGraphics graphics, int frameCount, int size) {
    graphics.noStroke();
    graphics.fill(255);
    graphics.rect(graphics.width / 2 - size / 2, graphics.height / 2 - size / 2, size, size);
  }

    @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    doDraw(graphics, frameCount, 1);
  }

  @Override
  public void preview(PGraphics graphics) {
    doDraw(graphics, 60, 40);
  }
}