package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class Spinning extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    float radius = 50;

    graphics.pushMatrix();
    graphics.translate(graphics.width / 2, graphics.height / 2);
    for (int i = 0; i < 10; i++) {
      float theta = (float) (i / 10f * 2 * Math.PI) + frameCount / 300f;
      graphics.fill(255);
      graphics.noStroke();
      graphics.rect((float) Math.cos(theta) * radius, (float) Math.sin(theta) * radius, 1, 1);
    }
    graphics.popMatrix();
  }
}
