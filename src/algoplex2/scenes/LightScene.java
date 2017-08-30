package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Quad;
import color.ColorProvider;
import color.ConstantColorProvider;
import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.ContourComponent;
import lusio.components.GraphComponent;
import particles.Bounds;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.Scene;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import java.util.Map;

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
