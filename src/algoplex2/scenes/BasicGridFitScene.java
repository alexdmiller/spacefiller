package algoplex2.scenes;

import processing.core.PGraphics;

public class BasicGridFitScene extends GridScene {
  float t = 0;

  public BasicGridFitScene() {
    // fitToGrid();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += 1;

    graphics.stroke(255);
    graphics.strokeWeight(1);
    for (float y = 0; y < grid.getHeight() * 2; y += 20) {
      float ny = (y + t) % grid.getHeight();

      graphics.line(0, ny, grid.getWidth(), ny);
    }
  }
}
