package algoplex2;

import graph.Edge;
import graph.Node;
import processing.core.*;

import javax.media.jai.PerspectiveTransform;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GraphTransformer implements Serializable {
  // This grid stores the original node positions, before a perspective transformation
  // is applied.
  private Grid preTransformGrid;
  private Quad preTransformQuad;

  // And this grid stores the nodes after the transformation has been applied.
  private Grid postTransformGrid;
  private Quad postTransformQuad;

  // Maps the post-transform nodes to pre-transform nodes
  private Map<Node, Node> postToPre;

  private PVector selectedQuadPoint;
  private Node selectedNode;

  public GraphTransformer(Grid grid) {
    this.postTransformGrid = grid;
    this.postTransformQuad = grid.getBoundingQuad();
    this.postToPre = new HashMap<>();
    makeGraphCopy();
  }

  public void drawUI(PGraphics graphics) {
    graphics.stroke(255);
    graphics.noFill();

    graphics.strokeWeight(2);
    graphics.beginShape();
    for (PVector node : postTransformQuad.getVertices()) {
      graphics.vertex(node.x, node.y);
    }
    graphics.endShape(PConstants.CLOSE);

    for (PVector node : postTransformQuad.getVertices()) {
      if (node == selectedQuadPoint) {
        graphics.fill(255, 0, 0);
      } else {
        graphics.noFill();
      }
      graphics.strokeWeight(3);
      graphics.ellipse(node.x, node.y, 25, 25);
    }

    for (Node node : postTransformGrid.getNodes()) {
      graphics.noFill();
      graphics.ellipse(node.position.x, node.position.y, 10, 10);
    }

    for (Edge e : postTransformGrid.getEdges()) {
      graphics.strokeWeight(1);
      graphics.line(e.n1.position.x, e.n1.position.y, e.n2.position.x, e.n2.position.y);
    }
  }

  public void mouseDown(float mouseX, float mouseY) {
    PVector mouse = new PVector(mouseX, mouseY);

    for (PVector quadPoint : postTransformQuad.getVertices()) {
      float dist = PVector.dist(quadPoint, mouse);
      if (dist < 10) {
        selectedQuadPoint = quadPoint;
        return;
      }
    }

    for (Node node : postTransformGrid.getNodes()) {
      float dist = PVector.dist(node.position, mouse);
      if (dist < 10) {
        selectedNode = node;
        return;
      }
    }
  }

  public Grid getPreTransformGrid() {
    return preTransformGrid;
  }

  public Grid getPostTransformGrid() {
    return postTransformGrid;
  }

  public void mouseUp(float mouseX, float mouseY) {
    selectedQuadPoint = null;
    selectedNode = null;
  }

  public void mouseDragged(float mouseX, float mouseY) {
    if (selectedQuadPoint != null) {
      selectedQuadPoint.x = mouseX;
      selectedQuadPoint.y = mouseY;

      PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
          preTransformQuad.getTopLeft().x, preTransformQuad.getTopLeft().y,
          preTransformQuad.getTopRight().x, preTransformQuad.getTopRight().y,
          preTransformQuad.getBottomRight().x, preTransformQuad.getBottomRight().y,
          preTransformQuad.getBottomLeft().x, preTransformQuad.getBottomLeft().y,
          postTransformQuad.getTopLeft().x, postTransformQuad.getTopLeft().y,
          postTransformQuad.getTopRight().x, postTransformQuad.getTopRight().y,
          postTransformQuad.getBottomRight().x, postTransformQuad.getBottomRight().y,
          postTransformQuad.getBottomLeft().x, postTransformQuad.getBottomLeft().y);

      float[] srcPoints = new float[postTransformGrid.getNodes().size() * 2];

      int i = 0;
      for (Node node : preTransformGrid.getNodes()) {
        srcPoints[i] = node.position.x;
        srcPoints[i + 1] = node.position.y;
        i += 2;
      }

      float[] destPoints = new float[postTransformGrid.getNodes().size() * 2];

      transform.transform(srcPoints, 0, destPoints, 0, postTransformGrid.getNodes().size());

      i = 0;
      for (Node node : postTransformGrid.getNodes()) {
        node.position.x = destPoints[i];
        node.position.y = destPoints[i + 1];
        i += 2;
      }
    } else if (selectedNode != null) {
      selectedNode.position.x = mouseX;
      selectedNode.position.y = mouseY;
    }
  }

  public void drawImage(PGraphics graphics, PImage texture) {
    graphics.beginShape(PApplet.TRIANGLES);
    graphics.noStroke();
    graphics.texture(texture);
    for (Node[] triangle : postTransformGrid.getTriangles()) {
      for (int i = 0; i < triangle.length; i++) {
        Node preNode = postToPre.get(triangle[i]);
        graphics.vertex(
            triangle[i].position.x, triangle[i].position.y,
            preNode.position.x, preNode.position.y);
      }
    }
    graphics.endShape(PConstants.CLOSE);
  }

  private void makeGraphCopy() {
    preTransformGrid = new Grid();

    for (Node postNode : postTransformGrid.getNodes()) {
      Node preNode = preTransformGrid.createNode(postNode.position.x, postNode.position.y);
      postToPre.put(postNode, preNode);
    }

    for (Edge e : postTransformGrid.getEdges()) {
      Node n1 = postToPre.get(e.n1);
      Node n2 = postToPre.get(e.n2);
      preTransformGrid.createEdge(n1, n2);
    }

    preTransformGrid.setBoundingQuad(postTransformQuad.copy());

    preTransformQuad = postTransformQuad.copy();
  }
}
