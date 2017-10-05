package graph.renderer;

import graph.Edge;
import graph.Graph;
import graph.Node;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.remote.Mod;

public class CrosshairGraphRenderer implements GraphRenderer {
  @Mod(min = 1, max = 10)
  public float thickness = 1;

  @Mod(min = 0, max = 100)
  public float size = 50;

  private int color = 0xFFFFFFFF;

  @Override
  public void render(PGraphics graphics, Graph graph) {
    graphics.noFill();
    graphics.strokeWeight(thickness);
    graphics.stroke(color);

//    for (Node n : graph.getNodes()) {
//      graphics.pushMatrix();
//      graphics.translate(n.position.x, n.position.y);
//      graphics.line(-size, 0, size, 0);
//      graphics.line(0, -size, 0, size);
//      graphics.popMatrix();
//    }

    for (Edge e : graph.getEdges()) {
      PVector delta = PVector.sub(e.n2.position, e.n1.position);
      float theta = (float) Math.atan2(delta.y, delta.x);
      graphics.pushMatrix();
      graphics.translate(e.n1.position.x, e.n1.position.y);
      graphics.rotate(theta);
      graphics.line(0, 0, size, 0);
      graphics.line(delta.mag() - size, 0, delta.mag(), 0);
      graphics.popMatrix();
    }
  }

  public void setColor(int color) {
    this.color = color;
  }
}
