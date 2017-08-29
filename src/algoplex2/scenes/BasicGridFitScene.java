package algoplex2.scenes;

import algoplex2.Algoplex2;
import graph.Node;
import processing.core.PGraphics;

/**
 * Created by miller on 8/29/17.
 */
public class BasicGridFitScene extends GridScene {
  float t = 0;

  public BasicGridFitScene() {
    fitToGrid();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += 2;

    graphics.stroke(255);
    graphics.strokeWeight(2);
    for (float y = 0; y < grid.getHeight() * 2; y += 20) {
      float ny = (y + t) % grid.getHeight();

      graphics.line(0, ny, grid.getWidth(), ny);
    }
  }
}
