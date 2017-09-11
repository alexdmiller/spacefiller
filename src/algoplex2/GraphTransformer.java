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

  // And this grid stores the nodes after the transformation has been applied.
  private Grid postTransformGrid;

  // Maps the post-transform nodes to pre-transform nodes
  private Map<Node, Node> postToPre;

  private PVector selectedQuadPoint;
  private Node selectedNode;

  public GraphTransformer(Grid grid) {
    this.postTransformGrid = grid;
    this.postToPre = new HashMap<>();
    makeGraphCopy();
  }

  public void drawUI(PGraphics graphics) {
    graphics.stroke(255);
    graphics.noFill();

    graphics.strokeWeight(2);
    graphics.beginShape();
    for (PVector node : postTransformGrid.getBoundingQuad().getVertices()) {
      graphics.vertex(node.x, node.y);
    }
    graphics.endShape(PConstants.CLOSE);

    for (PVector node : postTransformGrid.getBoundingQuad().getVertices()) {
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

    for (PVector quadPoint : postTransformGrid.getBoundingQuad().getVertices()) {
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

  public Node getPreNode(Node postNode) {
    return postToPre.get(postNode);
  }

  public void mouseUp(float mouseX, float mouseY) {
    selectedQuadPoint = null;
    selectedNode = null;
  }

  public void mouseDragged(float mouseX, float mouseY) {
    if (selectedQuadPoint != null) {
      selectedQuadPoint.x = mouseX;
      selectedQuadPoint.y = mouseY;

      Quad postQuad = postTransformGrid.getBoundingQuad();
      Quad preQuad = preTransformGrid.getBoundingQuad();

      PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
          preQuad.getTopLeft().position.x, preQuad.getTopLeft().position.y,
          preQuad.getTopRight().position.x, preQuad.getTopRight().position.y,
          preQuad.getBottomRight().position.x, preQuad.getBottomRight().position.y,
          preQuad.getBottomLeft().position.x, preQuad.getBottomLeft().position.y,
          postQuad.getTopLeft().position.x, postQuad.getTopLeft().position.y,
          postQuad.getTopRight().position.x, postQuad.getTopRight().position.y,
          postQuad.getBottomRight().position.x, postQuad.getBottomRight().position.y,
          postQuad.getBottomLeft().position.x, postQuad.getBottomLeft().position.y);

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

    preTransformGrid.setCellSize(postTransformGrid.getCellSize());
    preTransformGrid.setColumns(postTransformGrid.getColumns());
    preTransformGrid.setRows(postTransformGrid.getRows());

    for (Node postNode : postTransformGrid.getNodes()) {
      Node preNode = preTransformGrid.createNode(postNode.position.x, postNode.position.y);
      postToPre.put(postNode, preNode);
    }

    for (Edge e : postTransformGrid.getEdges()) {
      Node n1 = postToPre.get(e.n1);
      Node n2 = postToPre.get(e.n2);
      preTransformGrid.createEdge(n1, n2);
    }

    preTransformGrid.setBoundingQuad(postTransformGrid.getBoundingQuad().copy());

    for (Node[] triangle : postTransformGrid.getTriangles()) {
      Node[] newTriangle = new Node[3];
      for (int i = 0; i < triangle.length; i++) {
        newTriangle[i] = postToPre.get(triangle[i]);
      }
      preTransformGrid.addTriangle(newTriangle);
    }

    for (Quad quad : postTransformGrid.getSquares()) {
      preTransformGrid.addSquare(copyQuad(quad));
    }
  }

  private Quad copyQuad(Quad quad) {
    return new Quad(
        postToPre.get(quad.getTopLeft()),
        postToPre.get(quad.getTopRight()),
        postToPre.get(quad.getBottomRight()),
        postToPre.get(quad.getBottomLeft()),
        postToPre.get(quad.getCenter())
    );
  }
}
