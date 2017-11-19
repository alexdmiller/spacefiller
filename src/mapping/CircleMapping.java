package mapping;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import static java.lang.Math.PI;
import static processing.core.PConstants.TRIANGLE_FAN;

public class CircleMapping {
  private static final int DEFAULT_DIVISIONS = 20;
  private static final float DEFAULT_RADIUS = 400;

  private ShapeMapping mapping;
  private float radius;
  private int divisions;
  private PGraphics graphics;
  private PImage texture;

  public CircleMapping(PGraphics graphics, PImage texture) {
    this.graphics = graphics;
    this.texture = texture;
    this.radius = DEFAULT_RADIUS;
    this.divisions = DEFAULT_DIVISIONS;

    createShapeMapping();
    mapping.setPosition(new PVector(radius, radius));
  }

  private void createShapeMapping() {
    PShape shape = graphics.createShape();

    shape.beginShape(TRIANGLE_FAN);
    shape.vertex(0, 0);
    for (float theta = 0; theta <= PI * 2; theta += 2 * PI / divisions) {
      shape.vertex(
          (float) Math.cos(theta) * radius,
          (float) Math.sin(theta) * radius);
    }
    shape.endShape();

    this.mapping = new ShapeMapping(graphics, shape, texture);
    this.mapping.resize(mapping.getBoundingRect());
  }

  public void renderUI() {
    PVector position = mapping.getPosition();

    mapping.renderUI();

    graphics.pushMatrix();
    graphics.translate(position.x, position.y);
    graphics.noFill();
    graphics.stroke(255);
    graphics.strokeWeight(2);
    graphics.ellipse(0, 0, radius * 2, radius * 2);
    graphics.popMatrix();
  }

  public void render() {
    mapping.render();
  }

  public void mouseDown(float x, float y) {

  }

  public void mouseUp(float x, float y) {

  }

  public void mouseMoved(float x, float y) {
    PVector mouse = new PVector(x, y);
    float radius = mouse.dist(mapping.getPosition());

    Quad quad = new Quad(new PVector(-radius, -radius), new PVector(radius, radius));
    mapping.resize(quad);
  }
}
