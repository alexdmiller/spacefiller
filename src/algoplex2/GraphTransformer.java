package algoplex2;

import graph.Edge;
import graph.Node;
import processing.core.*;

import javax.media.jai.PerspectiveTransform;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by miller on 8/21/17.
 */
public class GraphTransformer {
  private Grid preTransformGrid;
  private Quad preTransformQuad;
  private Grid postTransformGrid;
  private Quad postTransformQuad;
  private PVector selected;
  private Map<Node, Node> postToPre;

  public GraphTransformer(Grid grid) {
    this.postTransformGrid = grid;
    this.postTransformQuad = grid.getBoundingQuad();
    this.postToPre = new HashMap<>();
    makeGraphCopy();
  }

  public void draw(PGraphics graphics) {
    graphics.stroke(255);
    graphics.noFill();

    graphics.strokeWeight(2);
    graphics.beginShape();
    for (PVector node : postTransformQuad.getVertices()) {
      graphics.vertex(node.x, node.y);
    }
    graphics.endShape(PConstants.CLOSE);

    for (PVector node : postTransformQuad.getVertices()) {
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

    for (PVector node : postTransformQuad.getVertices()) {
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
    }
  }

  public void drawImage(PGraphics graphics, PImage texture) {
    graphics.beginShape(PApplet.TRIANGLES);
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

  public void makeGraphCopy() {
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

    preTransformQuad = postTransformQuad.copy();
  }
}
