package graph.renderer;

import graph.Edge;
import graph.Graph;
import graph.Node;
import processing.core.PGraphics;

public class BasicGraphRenderer implements GraphRenderer {
  private float thickness;
  private int color;

  public BasicGraphRenderer(float thickness) {
    this.thickness = thickness;
  }

  @Override
  public void render(PGraphics graphics, Graph graph) {
    graphics.noFill();
    graphics.strokeWeight(thickness);
    graphics.stroke(color);

    for (Node n : graph.getNodes()) {
      graphics.ellipse(n.position.x, n.position.y, 10, 10);
    }

    for (Edge e: graph.getEdges()) {
      graphics.line(e.n1.position.x, e.n1.position.y, e.n2.position.x, e.n2.position.y);
    }
  }

  public void setColor(int color) {
    this.color = color;
  }
}
