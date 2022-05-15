package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class Rectangle extends Animator.SimpleAnimator {
  private int width;
  private int height;

  public Rectangle(int width, int height) {
    this.width = width;
    this.height = height;
  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight) {
    graphics.rectMode(PConstants.CENTER);
    graphics.stroke(255);
    graphics.noFill();
    graphics.strokeWeight(strokeWeight);
    graphics.rect(graphics.width / 2, graphics.height / 2, width, height);
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
