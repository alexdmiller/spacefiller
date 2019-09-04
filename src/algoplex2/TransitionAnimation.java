package algoplex2;

import processing.core.PVector;
import spacefiller.graph.Edge;
import spacefiller.graph.renderer.CrosshairGraphRenderer;
import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.mapping.Grid;

import java.util.Iterator;

/**
 * Created by miller on 10/4/17.
 */
public class TransitionAnimation {
  private float t;
  private static final int MID_POINT = 100;

  CrosshairGraphRenderer crosshairGraphRenderer;

  public TransitionAnimation() {
    crosshairGraphRenderer = new CrosshairGraphRenderer();
  }

  public void reset() {
    t = 0;
    crosshairGraphRenderer.size = 100 - t;
  }

  public void draw(PGraphics graphics, Grid grid) {
    t++;

    graphics.colorMode(PConstants.RGB);
    graphics.noFill();
    graphics.strokeWeight(2);
    graphics.stroke(255);
    Iterator var3 = grid.getEdges().iterator();

    int edgeIndex = 0;
    while(var3.hasNext()) {
      Edge e = (Edge)var3.next();
      PVector delta = PVector.sub(e.n2.position, e.n1.position);
      float theta = (float)Math.atan2((double)delta.y, (double)delta.x);

      float size = Math.max(0, edgeIndex + t * 20 - 300);
      float alpha = Math.max(Math.min(1, 1 - size / 200f), 0);
      graphics.pushMatrix();
      graphics.translate(e.n1.position.x, e.n1.position.y);
      graphics.rotate(theta);
      graphics.stroke(255, 255 * alpha);
      graphics.line(0.0F, 0.0F, size, 0.0F);
      graphics.line(delta.mag() - size, 0.0F, delta.mag(), 0.0F);
      graphics.popMatrix();

      edgeIndex++;
    }
  }
}
