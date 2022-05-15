package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

import static crystal.maps.Constants.COLORS;

public class Interlaced extends Animator.SimpleAnimator {
  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    int c = 0;
    for (int i = 0; i < graphics.height; i += 1) {
      int y = (i + frameCount / 10) % graphics.height;

      graphics.stroke(COLORS[c % 3]);
      graphics.strokeWeight(1);
      graphics.line(0, y, graphics.width, y);

      c++;
    }
  }
}
