package graph.renderer;

import graph.Edge;
import graph.Graph;
import graph.Node;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import processing.core.PVector;

public class SinGraphRenderer implements GraphRenderer {

  @Mod(min = -0.5f, max = 0.5f)
  public float speed = 0.1f;

  @Mod(min = 0, max = 10)
  public float size = 5;

  @Mod(min = 0, max = 20)
  public float freq = 10;

  private float time;
  private float spacing = 5;
  private float thickness = 3;
  private int color;

  public void setThickness(float thickness) {
    this.thickness = thickness;
  }

  @Override
  public void render(PGraphics graphics, Graph graph) {
    time += speed;

    graphics.strokeWeight(thickness);
    graphics.stroke(color);
    for (Edge e : graph.getEdges()) {
      PVector delta = PVector.sub(e.n2.position, e.n1.position);
      graphics.pushMatrix();
      graphics.translate(e.n1.position.x, e.n1.position.y);
      graphics.rotate(delta.heading());

      for (float t = 0; t < delta.mag(); t += spacing) {
        graphics.line(t, func(t, delta.mag()), t + spacing, func(t + spacing, delta.mag()));
      }

      graphics.popMatrix();
    }

    for (Node n : graph.getNodes()) {
      graphics.strokeWeight(4);
      graphics.point(n.position.x, n.position.y);
    }
  }

  public void setSize(float size) {
    this.size = size;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public void setFreq(float freq) {
    this.freq = freq;
  }

  float func(float t, float total) {
    return (float) (Math.sin(t / freq + time) * size * Math.sin(t / total * Math.PI));
  }

  public void setColor(int color) {
    this.color = color;
  }
}
