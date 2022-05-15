package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class Color extends Animator.SimpleAnimator {
  private int color;
  private int width;
  private int height;

  public Color(int color) {
    this.color = color;
  }

  public Color(int color, int width, int height ) {
    this.color = color;
    this.width = width;
    this.height = height;
  }

  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    if (width == 0 || height == 0) {
      graphics.background(color);
    } else {
      graphics.rectMode(PConstants.CENTER);
      graphics.noStroke();
      graphics.fill(color);
      graphics.rect(graphics.width / 2, graphics.height / 2, width, height);
    }
  }
}
