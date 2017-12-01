package mapping;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import javax.media.jai.PerspectiveTransform;

public class ShapeMapping {
  private static final float VERTEX_HANDLE_RADIUS = 10;

  private PGraphics graphics;
  private PImage texture;

  private PShape sourceShape;
  private PShape targetShape;

  private int selectedIndex = -1;

  private PVector pos = new PVector();

  public ShapeMapping(PGraphics graphics, PShape shape, PImage texture) {
    this.graphics = graphics;
    this.sourceShape = shape;
    this.texture = texture;

    this.targetShape = graphics.createShape();
    targetShape.beginShape(shape.getKind());
    targetShape.noStroke();
    for (int i = 0; i < shape.getVertexCount(); i++) {
      targetShape.vertex(sourceShape.getVertexX(i), sourceShape.getVertexY(i), sourceShape.getVertexX(i), sourceShape.getVertexY(i));
    }
    targetShape.endShape();
    targetShape.setTexture(texture);
  }

  public PShape getSourceShape() {
    return sourceShape;
  }

  public void setPosition(PVector newPos) {
    PVector diff = PVector.sub(newPos, pos);
    targetShape.translate(diff.x, diff.y);
    this.pos = newPos;
  }

  public Quad getBoundingRect() {
    PVector vertex = targetShape.getVertex(0);
    PVector topLeft = new PVector(vertex.x, vertex.y);
    PVector bottomRight = new PVector(vertex.x, vertex.y);

    for (int i = 1; i < targetShape.getVertexCount(); i++) {
      vertex = targetShape.getVertex(i);
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

    float[] srcPoints = new float[targetShape.getVertexCount() * 2];
    float[] destPoints = new float[targetShape.getVertexCount() * 2];

    for (int i = 0; i < targetShape.getVertexCount(); i++) {
      PVector v = targetShape.getVertex(i);
      srcPoints[i * 2] = v.x;
      srcPoints[i * 2 + 1] = v.y;
    }

    transform.transform(srcPoints, 0, destPoints, 0, targetShape.getVertexCount());

    for (int i = 0; i < targetShape.getVertexCount(); i++) {
      targetShape.setVertex(i, destPoints[i * 2], destPoints[i * 2 + 1]);
    }
  }

  public PVector getPosition() {
    return pos;
  }

  public void renderUI() {
    graphics.noFill();
    graphics.stroke(255, 0, 0);
    graphics.strokeWeight(2);
    graphics.pushMatrix();
    graphics.translate(pos.x, pos.y);

    for (int i = 0; i < targetShape.getVertexCount(); i++) {
      PVector v = targetShape.getVertex(i);
      graphics.ellipse(v.x, v.y, VERTEX_HANDLE_RADIUS, VERTEX_HANDLE_RADIUS);
    }

    graphics.popMatrix();
  }

  public void render() {
    graphics.shape(targetShape);
  }

  public void mouseDown(float x, float y) {
    PVector mouse = new PVector(x, y);
    for (int i = 0; i < targetShape.getVertexCount(); i++) {
      PVector v = targetShape.getVertex(i);
      v.add(pos.x, pos.y);
      if (mouse.dist(v) < VERTEX_HANDLE_RADIUS) {
        selectedIndex = i;
        break;
      }
    }
  }

  public void mouseUp(float x, float y) {
    selectedIndex = -1;
  }

  public void mouseMoved(float x, float y) {
    if (selectedIndex >= 0) {
      targetShape.setVertex(selectedIndex, x - pos.x, y - pos.y);
    }
  }
}
