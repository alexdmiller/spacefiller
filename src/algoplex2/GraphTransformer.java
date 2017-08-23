package algoplex2;

import graph.Graph;
import graph.Node;
import particles.Bounds;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import javax.media.jai.PerspectiveTransform;
/**
 * Created by miller on 8/21/17.
 */
public class GraphTransformer {
  private Graph graph;
  private Quad quad;
  private PVector selected;

  public GraphTransformer(Graph graph, Quad quad) {
    this.graph = graph;
    this.quad = quad;
  }

  public void draw(PGraphics graphics) {
    graphics.stroke(255);
    graphics.noFill();

    graphics.strokeWeight(2);
    graphics.beginShape();
    for (PVector node : quad.getNodes()) {
      graphics.vertex(node.x, node.y);
    }
    graphics.endShape(PConstants.CLOSE);

    for (PVector node : quad.getNodes()) {
      if (node == selected) {
        graphics.fill(255, 0, 0);
      } else {
        graphics.noFill();
      }
      graphics.strokeWeight(3);
      graphics.ellipse(node.x, node.y, 25, 25);
    }
  }

  public void mouseDown(float mouseX, float mouseY) {
    PVector mouse = new PVector(mouseX, mouseY);

    for (PVector node : quad.getNodes()) {
      float dist = PVector.dist(node, mouse);
      if (dist < 50) {
        selected = node;
        return;
      }
    }
  }

  public void mouseUp(float mouseX, float mouseY) {
    selected = null;
  }

  public void mouseDragged(float mouseX, float mouseY) {
    if (selected != null) {
      Quad oldQuad = quad.copy();

      selected.x = mouseX;
      selected.y = mouseY;

      PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
          oldQuad.getTopLeft().x, oldQuad.getTopLeft().y,
          oldQuad.getTopRight().x, oldQuad.getTopRight().y,
          oldQuad.getBottomRight().x, oldQuad.getBottomRight().y,
          oldQuad.getBottomLeft().x, oldQuad.getBottomLeft().y,
          quad.getTopLeft().x, quad.getTopLeft().y,
          quad.getTopRight().x, quad.getTopRight().y,
          quad.getBottomRight().x, quad.getBottomRight().y,
          quad.getBottomLeft().x, quad.getBottomLeft().y);

      float[] srcPoints = new float[graph.getNodes().size() * 2];

      int i = 0;
      for (Node node : graph.getNodes()) {
        srcPoints[i] = node.position.x;
        srcPoints[i + 1] = node.position.y;
        i += 2;
      }

      float[] destPoints = new float[graph.getNodes().size() * 2];

      transform.transform(srcPoints, 0, destPoints, 0, graph.getNodes().size());

      i = 0;
      for (Node node : graph.getNodes()) {
        node.position.x = destPoints[i];
        node.position.y = destPoints[i + 1];
        i += 2;
      }
    }
  }

  private void transformGraph() {

  }
}
