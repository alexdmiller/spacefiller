package mapping;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;
import toxi.geom.Rect;
import toxi.geom.Vec2D;

import javax.media.jai.PerspectiveTransform;

public class ShapeMapping {
  private static final float VERTEX_HANDLE_RADIUS = 20;

  private PGraphics graphics;
  private PImage texture;

  private PShape preShape;
  private PShape postShape;

  private int selectedIndex = -1;

  private PVector lastMouse = new PVector(0, 0);
  private PVector pos = new PVector();

  public ShapeMapping(PGraphics graphics, PShape shape, PImage texture) {
    this.graphics = graphics;
    this.preShape = shape;
    this.texture = texture;

    this.postShape = graphics.createShape();
    postShape.beginShape(shape.getKind());
    postShape.noStroke();
    for (int i = 0; i < shape.getVertexCount(); i++) {
      postShape.vertex(preShape.getVertexX(i), preShape.getVertexY(i), preShape.getVertexX(i), preShape.getVertexY(i));
    }
    postShape.endShape();
    postShape.setTexture(texture);
  }

  public void setPosition(PVector newPos) {
    PVector diff = PVector.sub(newPos, pos);
    postShape.translate(diff.x, diff.y);
    this.pos = newPos;
  }

  public Quad getBoundingRect() {
    PVector vertex = postShape.getVertex(0);
    PVector topLeft = new PVector(vertex.x, vertex.y);
    PVector bottomRight = new PVector(vertex.x, vertex.y);

    for (int i = 1; i < postShape.getVertexCount(); i++) {
      vertex = postShape.getVertex(i);
      topLeft.x = Math.min(topLeft.x, vertex.x);
      topLeft.y = Math.min(topLeft.y, vertex.y);
      bottomRight.x = Math.max(bottomRight.x, vertex.x);
      bottomRight.y = Math.max(bottomRight.y, vertex.y);
    }

    return new Quad(topLeft, bottomRight);
  }

  public void resize(Quad target) {
    Quad current = getBoundingRect();

    PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
        current.topLeft.x, current.topLeft.y,
        current.topRight.x, current.topRight.y,
        current.bottomRight.x, current.bottomRight.y,
        current.bottomLeft.x, current.bottomLeft.y,
        target.topLeft.x, target.topLeft.y,
        target.topRight.x, target.topRight.y,
        target.bottomRight.x, target.bottomRight.y,
        target.bottomLeft.x, target.bottomLeft.y);

    float[] srcPoints = new float[postShape.getVertexCount() * 2];
    float[] destPoints = new float[postShape.getVertexCount() * 2];

    for (int i = 0; i < postShape.getVertexCount(); i++) {
      PVector v = postShape.getVertex(i);
      srcPoints[i * 2] = v.x;
      srcPoints[i * 2 + 1] = v.y;
    }

    transform.transform(srcPoints, 0, destPoints, 0, postShape.getVertexCount());

    for (int i = 0; i < postShape.getVertexCount(); i++) {
      postShape.setVertex(i, destPoints[i * 2], destPoints[i * 2 + 1]);
    }
  }

  /*

      if (selectedQuadPoint != null) {
      selectedQuadPoint.x = mouseX;
      selectedQuadPoint.y = mouseY;

      Quad postQuad = postTransformGrid.getBoundingQuad();
      Quad preQuad = preTransformGrid.getBoundingQuad();



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
   */

  public PVector getPosition() {
    return pos;
  }

  public void renderUI() {
    graphics.noFill();
    graphics.stroke(255, 0, 0);
    graphics.pushMatrix();
    graphics.translate(pos.x, pos.y);

    for (int i = 0; i < postShape.getVertexCount(); i++) {
      PVector v = postShape.getVertex(i);
      graphics.ellipse(v.x, v.y, VERTEX_HANDLE_RADIUS, VERTEX_HANDLE_RADIUS);
    }

    Quad rect = getBoundingRect();
    graphics.quad(
        rect.topLeft.x, rect.topLeft.y,
        rect.topRight.x, rect.topRight.y,
        rect.bottomRight.x, rect.bottomRight.y,
        rect.bottomLeft.x, rect.bottomLeft.y);

    graphics.popMatrix();
  }

  public void render() {
    graphics.shape(postShape);
  }

  public void mouseDown(float x, float y) {
    PVector mouse = new PVector(x, y);
    for (int i = 0; i < postShape.getVertexCount(); i++) {
      PVector v = postShape.getVertex(i);
      v.add(pos.x, pos.y);
      if (mouse.dist(v) < VERTEX_HANDLE_RADIUS) {
        selectedIndex = i;
        break;
      }
    }

    if (selectedIndex == -1) {
      lastMouse = new PVector(x, y);
    }
  }

  public void mouseUp(float x, float y) {
    selectedIndex = -1;
  }

  public void mouseMoved(float x, float y) {
    if (selectedIndex >= 0) {
      postShape.setVertex(selectedIndex, x - pos.x, y - pos.y);
    } else if (lastMouse != null) {
      setPosition(new PVector(x, y));

      lastMouse.x = x;
      lastMouse.y = y;
    }
  }
}
