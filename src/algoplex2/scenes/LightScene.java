package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import spacefiller.remote.Mod;
import processing.core.PGraphics;

public class LightScene extends GridScene {
  private float t = 0;

  @Mod(min = 0, max = 10)
  public float shiftAmount = 0;

  @Mod(min = 0, max = 10)
  public float mod1 = 0;

  @Mod(min = 0, max = 10)
  public float mod2 = 0;

  @Mod(min = -0.05f, max = 0.05f)
  public float speed = 0.01f;

  public LightScene() {
    fitToGrid();
  }

  @Override
  public void draw(PGraphics graphics) {
//    shiftAmount = controller.getValue(0);
//    mod1 = controller.getValue(1);
//    mod2 = controller.getValue(2);

    t += speed;

    float shift = t + shiftAmount;

    graphics.noStroke();
    graphics.noFill();
    int quadIndex = 0;
    for (Quad quad : grid.getSquares()) {
      int triangleIndex = 0;
      for (Node[] triangle : quad.getTriangles()) {
        float v = (float) ((Math.sin(
            quadIndex * mod1 +
            triangleIndex / 4f * Math.PI * 2 * mod2
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
