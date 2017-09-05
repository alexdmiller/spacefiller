package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Grid;
import algoplex2.Quad;
import graph.Node;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Created by miller on 8/23/17.
 */
public class PsychScene extends GridScene {
  private float t;
  private static int NUM_SQUARES = 5;

  public PsychScene() {
    fitToGrid();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += 0.01f;
    graphics.noStroke();
//
//
//    graphics.noStroke();
//    int i = 0;
//    for (Node[] triangle : grid.getTriangles()) {
//      i++;
//      graphics.fill(Algoplex2.instance.noise(i + t) * 255, Algoplex2.instance.noise(0, i + t) * 255, Algoplex2.instance.noise(0, 0, i + t) * 255);
//      graphics.triangle(
//          triangle[0].position.x, triangle[0].position.y,
//          triangle[1].position.x, triangle[1].position.y,
//          triangle[2].position.x, triangle[2].position.y);
//    }
//
    graphics.noFill();
    graphics.stroke(255);
    graphics.rectMode(PConstants.CENTER);
    graphics.strokeWeight(5);

    float s = t * 50;
    for (Quad square : grid.getSquares()) {
      float totalWidth = (square.getTopRight().position.x - square.getTopLeft().position.x);
      float totalHeight = square.getBottomLeft().position.y - square.getTopLeft().position.y;
      for (int j = 0; j < NUM_SQUARES; j++) {
        graphics.stroke(255);
        graphics.pushMatrix();
        graphics.translate(square.getCenter().position.x, square.getCenter().position.y);
        graphics.rect(0, 0, ((((float) j / NUM_SQUARES) * totalWidth) + s) % totalWidth, ((((float) j / NUM_SQUARES) * totalWidth) + s) % totalWidth);
        graphics.popMatrix();
      }
    }
//    graphics.rectMode(PConstants.CORNER);
    super.draw(graphics);
  }
}
