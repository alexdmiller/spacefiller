package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class Spinner extends Animator.SimpleAnimator {
  private int size;
  private int height;

  public Spinner(int size) {
    this.size = size;
  }

  private void doDraw(PGraphics graphics, int frameCount, int strokeWeight) {
    graphics.stroke(255);
    graphics.noFill();
    graphics.strokeWeight(strokeWeight);
    int middleX = graphics.width / 2;
    int middleY = graphics.height / 2;
    graphics.line((float) (middleX+size*Math.cos(frameCount / 10f)), (float) (middleY+size*Math.sin(frameCount / 10f)), middleX, middleY);

    graphics.noStroke();
    graphics.fill(255, 0, 0);
    graphics.rect(middleX, middleY, 1, 1);
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
