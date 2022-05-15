package spacefiller.crystals.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

import static crystal.maps.Constants.COLORS;

public class CircleZoom extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.ellipseMode(PConstants.CENTER);
    graphics.noFill();

    for (int i = 6; i > 0; i--) {
      graphics.stroke(COLORS[i % 3]);
      int size = (int) ((float) frameCount / 2 + i * graphics.width / 3) % (graphics.width * 2);
      graphics.strokeWeight(Math.min(size, 20));
      graphics.ellipse(graphics.width / 2, graphics.height / 2, size, size);
    }
  }
}
