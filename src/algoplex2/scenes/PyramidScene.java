package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Grid;
import algoplex2.Quad;
import graph.Node;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class PyramidScene extends GridScene {
  private float t;
  private static int NUM_SQUARES = 5;

  @Override
  public void draw(PGraphics graphics) {
    t += 0.01f;
    graphics.stroke(255);
    graphics.noFill();
    graphics.ortho();

    for (Quad square : grid.getSquares()) {
      graphics.pushMatrix();

      graphics.translate(square.getCenter().position.x, square.getCenter().position.y);
      graphics.rotateX((float) (controller.getValue(0) * Math.PI * 2));
      graphics.rotateY((float) (controller.getValue(1) * Math.PI * 2));
      graphics.rotateZ((float) (controller.getValue(2) * Math.PI * 2));


      float size = square.getWidth() / 2;
      graphics.beginShape();
      graphics.vertex(-size, -size, -size);
      graphics.vertex(size, -size, -size);
      graphics.vertex(0, 0, size);

      graphics.vertex(size, -size, -size);
      graphics.vertex(size, size, -size);
      graphics.vertex(0, 0, size);

      graphics.vertex(size, size, -size);
      graphics.vertex(-size, size, -size);
      graphics.vertex(0, 0, size);

      graphics.vertex(-size, size, -size);
      graphics.vertex(-size, -size, -size);
      graphics.vertex(0, 0, size);
      graphics.endShape();
      graphics.popMatrix();
    }

    super.draw(graphics);
  }
}
