package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class Cube extends Animator.DoubleAnimator {

  @Override
  public void drawMap(PGraphics graphics, int frameCount, List<Integer> notes) {
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.fill(Constants.R);
    graphics.stroke(0);
    graphics.strokeWeight(2);
    graphics.rotateX(frameCount / 100f);
    graphics.rotateY(frameCount / 50f);
    graphics.box(200, 100, 100);
  }

  @Override
  public void drawSeed(PGraphics graphics, int frameCount, List<Integer> notes) {
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.fill(0);
    graphics.stroke(255);
    graphics.strokeWeight(2);
    graphics.rotateX(frameCount / 100f);
    graphics.rotateY(frameCount / 50f);
    graphics.box(200, 100, 100);
  }

  @Override
  public void setup() {

  }
}
