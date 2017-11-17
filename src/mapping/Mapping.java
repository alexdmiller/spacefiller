package mapping;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class Mapping {
  private static final float VERTEX_HANDLE_RADIUS = 20;

  private PGraphics graphics;
  private PShape preShape;
  private PShape postShape;
  private int selectedIndex = -1;

  public Mapping(PGraphics graphics, PShape shape) {
    this.graphics = graphics;
    this.preShape = shape;
    this.postShape = graphics.createShape();

    postShape.beginShape();
    for (int i = 0; i < preShape.getVertexCount(); i++) {
      PVector preVertex = preShape.getVertex(i);
      postShape.vertex(preVertex.x, preVertex.y, preVertex.x, preVertex.y);
    }
    postShape.endShape();
  }

  public void renderUI() {
    postShape.disableStyle();
    graphics.stroke(255);
    graphics.strokeWeight(1);
    graphics.noFill();
    graphics.shape(postShape);

    for (int i = 0; i < postShape.getVertexCount(); i++) {
      PVector v = postShape.getVertex(i);
      graphics.ellipse(v.x, v.y, VERTEX_HANDLE_RADIUS, VERTEX_HANDLE_RADIUS);
    }
  }

  public void render(PImage texture) {

  }

  public void mouseDown(float x, float y) {
    PVector mouse = new PVector(x, y);
    for (int i = 0; i < postShape.getVertexCount(); i++) {
      PVector v = postShape.getVertex(i);
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
      postShape.setVertex(selectedIndex, x, y);
    }
  }
}
