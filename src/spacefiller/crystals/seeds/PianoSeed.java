package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class PianoSeed extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.noStroke();
    graphics.fill(255);
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.rectMode(PConstants.CENTER);

    for (Integer i : notes) {
      int size = ((i % 12) + 1) * 10;
      graphics.noFill();
      graphics.stroke(255);
      graphics.strokeWeight(1);
      graphics.rect(0, 0, size, size);
    }
  }
}
