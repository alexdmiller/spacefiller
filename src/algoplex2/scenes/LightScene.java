package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import processing.core.PGraphics;

public class LightScene extends GridScene {
  private float t = 0;

  public LightScene() {
    fitToGrid();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += 0.01;

    float shift = t + controller.getValue(0) * 10;

    graphics.noStroke();
    graphics.noFill();
    int quadIndex = 0;
    for (Quad quad : grid.getSquares()) {
      int triangleIndex = 0;
      for (Node[] triangle : quad.getTriangles()) {
        float v = (float) ((Math.sin(
            quadIndex * controller.getValue(1) * 10 +
            triangleIndex / 4f * Math.PI * 2 * controller.getValue(2) * 10
            + shift) + 1) / 2);

        graphics.fill(v * v * 255);
        graphics.beginShape();
        for (Node n : triangle) {
          graphics.vertex(n.position.x, n.position.y);
        }
        graphics.endShape();

        triangleIndex++;
      }

      quadIndex++;
    }

    super.draw(graphics);
  }
}
