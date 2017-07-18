package graph;

import processing.core.PGraphics;
import processing.core.PVector;

public class DottedLineGraphRenderer implements GraphRenderer {
  private float time;
  private float spacing = 40;
  private float size = 20;
  private float scrollSpeed = 0.5f;

  @Override
  public void render(PGraphics graphics, Graph graph) {
    time += scrollSpeed;

    graphics.strokeWeight(1);
    for (Edge e : graph.getEdges()) {
      PVector delta = PVector.sub(e.n2.position, e.n1.position);
      graphics.pushMatrix();
      graphics.translate(e.n1.position.x, e.n1.position.y);
      graphics.rotate(delta.heading());

      for (float t = 0; t < delta.mag() + spacing * 2; t += spacing) {
        float pos = (t + time) % (delta.mag() + spacing * 2) - spacing;
        graphics.line(
            Math.min(Math.max(pos, 0), delta.mag()), 0,
            Math.min(Math.max(pos + size, 0), delta.mag()), 0);
      }

      graphics.popMatrix();
    }

    for (Node n : graph.getNodes()) {
      graphics.strokeWeight(4);
      graphics.point(n.position.x, n.position.y);
    }
  }
}
