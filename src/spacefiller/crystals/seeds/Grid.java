package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;

import java.util.List;

public class Grid extends Animator.SimpleAnimator {
  private int rows;
  private int cols;
  private int pointSize;

  public Grid(int rows, int cols, int pointSize) {
    this.rows = rows;
    this.cols = cols;
    this.pointSize = pointSize;
  }

  @Override
  public void setup() {

  }

  private void doDraw(PGraphics graphics, int frameCount, int size) {
    graphics.noStroke();
    graphics.fill(255);

    int colSpacing = graphics.width / cols;
    int rowSpacing = graphics.height / rows;

    for (int i = 0; i < graphics.width / cols; i++) {
      for (int j = 0; j < graphics.height / rows; j++) {
        int x = i * colSpacing - size / 2 - colSpacing / 2;
        int y = j * rowSpacing - size / 2 - rowSpacing / 2;

        if (x > 0 && x < graphics.width &&
            y > 0 && y < graphics.height
        ) {
          graphics.rect(
              i * colSpacing - size / 2 - colSpacing / 2,
              j * rowSpacing - size / 2 - rowSpacing / 2,
              size,
              size);
        }
      }
    }
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    doDraw(graphics, frameCount, pointSize);
  }

  @Override
  public void preview(PGraphics graphics) {
    doDraw(graphics, 60, 40);
  }
}