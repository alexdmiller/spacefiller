package algoplex2;

import graph.Edge;
import graph.Graph;
import graph.Node;
import processing.core.*;

import javax.media.jai.PerspectiveTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miller on 8/21/17.
 */
public class GraphTransformer {
  private Grid originalGrid;
  private Quad originalQuad;
  private Grid grid;
  private Quad quad;
  private PVector selected;
  private Map<Node, Node> oldToNew;

  public GraphTransformer(Grid grid) {
    this.grid = grid;
    this.quad = grid.getBoundingQuad();
    this.oldToNew = new HashMap<>();
    makeGraphCopy();
  }

  public void draw(PGraphics graphics) {
    graphics.stroke(255);
    graphics.noFill();

    graphics.strokeWeight(2);
    graphics.beginShape();
    for (PVector node : quad.getVertices()) {
      graphics.vertex(node.x, node.y);
    }
    graphics.endShape(PConstants.CLOSE);

    for (PVector node : quad.getVertices()) {
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

    for (PVector node : quad.getVertices()) {
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
      selected.x = mouseX;
      selected.y = mouseY;

      PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
          originalQuad.getTopLeft().x, originalQuad.getTopLeft().y,
          originalQuad.getTopRight().x, originalQuad.getTopRight().y,
          originalQuad.getBottomRight().x, originalQuad.getBottomRight().y,
          originalQuad.getBottomLeft().x, originalQuad.getBottomLeft().y,
          quad.getTopLeft().x, quad.getTopLeft().y,
          quad.getTopRight().x, quad.getTopRight().y,
          quad.getBottomRight().x, quad.getBottomRight().y,
          quad.getBottomLeft().x, quad.getBottomLeft().y);

      float[] srcPoints = new float[grid.getNodes().size() * 2];

      int i = 0;
      for (Node node : originalGrid.getNodes()) {
        srcPoints[i] = node.position.x;
        srcPoints[i + 1] = node.position.y;
        i += 2;
      }

      float[] destPoints = new float[grid.getNodes().size() * 2];

      transform.transform(srcPoints, 0, destPoints, 0, grid.getNodes().size());

      i = 0;
      for (Node node : grid.getNodes()) {
        node.position.x = destPoints[i];
        node.position.y = destPoints[i + 1];
        i += 2;
      }
    }
  }

  private void transformGraph() {

  }

  public void drawImage(PGraphics graphics, PImage texture) {
    graphics.beginShape(PApplet.TRIANGLES);
    graphics.texture(texture);
    // for (Node[] triangle : gr)
  }

  public void makeGraphCopy() {
    originalGrid = new Grid();

    for (Node n : grid.getNodes()) {
      Node originalNode = originalGrid.createNode(n.position.x, n.position.y);
      oldToNew.put(n, originalNode);
    }

    for (Edge e : grid.getEdges()) {
      Node n1 = oldToNew.get(e.n1);
      Node n2 = oldToNew.get(e.n2);
      originalGrid.createEdge(n1, n2);
    }

    originalQuad = quad.copy();
  }
}
