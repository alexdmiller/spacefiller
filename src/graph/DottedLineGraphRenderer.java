package graph;

import processing.core.PGraphics;
import processing.core.PVector;

public class DottedLineGraphRenderer implements GraphRenderer {
  private float time;
  private float spacing = 40;
  private float size = 20;
  private float scrollSpeed = 0.5f;
  private float thickness = 2;
  private int color;

  @Override
  public void render(PGraphics graphics, Graph graph) {
    time += scrollSpeed;

    graphics.strokeWeight(thickness);
    graphics.stroke(color);
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

  public void setColor(int color) {
    this.color = color;
  }

  public void setThickness(float thickness) {
    this.thickness = thickness;
  }

  public void setScrollSpeed(float scrollSpeed) {
    this.scrollSpeed = scrollSpeed;
  }

  public void setSize(float size) {
    this.size = size;
  }

  public void setSpacing(float spacing) {
    this.spacing = spacing;
  }
}
