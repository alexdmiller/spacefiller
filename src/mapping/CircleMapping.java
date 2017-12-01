package mapping;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

import static java.lang.Math.PI;
import static processing.core.PConstants.TRIANGLE_FAN;

public class CircleMapping {
  private static final int DEFAULT_DIVISIONS = 20;
  private static final float DEFAULT_RADIUS = 500;

  private enum Mode {
    RENDER, MOVE, RESIZE, EDIT
  }

  private ShapeMapping mapping;
  private float sourceRadius;
  private float targetRadius;
  private int divisions;
  private PGraphics graphics;
  private PImage source;
  private Mode mode;
  private PVector lastMouse;

  public CircleMapping(PGraphics graphics, PImage texture) {
    this(graphics, texture, DEFAULT_RADIUS);
    createShapeMapping();
  }

  public CircleMapping(PGraphics graphics, PImage texture, float sourceRadius) {
    this.graphics = graphics;
    this.source = texture;
    this.sourceRadius = this.targetRadius = sourceRadius;
    this.divisions = DEFAULT_DIVISIONS;
    this.mode = Mode.RENDER;

    createShapeMapping();
  }

  public PShape getSourceShape() {
    return mapping.getSourceShape();
  }

  private void createShapeMapping() {
    PShape shape = graphics.createShape();

    shape.beginShape(TRIANGLE_FAN);
    shape.vertex(sourceRadius, sourceRadius);
    for (float theta = 0; theta <= 2 * PI; theta += 2 * PI / divisions) {
      shape.vertex(
          sourceRadius + (float) Math.cos(theta) * sourceRadius,
          sourceRadius + (float) Math.sin(theta) * sourceRadius);
    }
    shape.vertex(
        sourceRadius + (float) Math.cos(0) * sourceRadius,
        sourceRadius + (float) Math.sin(0) * sourceRadius);

    shape.endShape();

    this.mapping = new ShapeMapping(graphics, shape, source);
    resize(targetRadius);
    this.mapping.setPosition(new PVector(targetRadius, targetRadius));
  }

  public void resize(float radius) {
    Quad quad = new Quad(new PVector(-radius, -radius), new PVector(radius, radius));
    mapping.resize(quad);
    this.targetRadius = radius;
  }

  public void setDivisions(int divisions) {
    this.divisions = divisions;
    createShapeMapping();
  }

  public void setPosition(PVector position) {
    mapping.setPosition(position);
  }

  public void renderUI() {
    PVector position = mapping.getPosition();

    if (mode == Mode.EDIT) {
      mapping.renderUI();
    } else {
      graphics.pushMatrix();
      graphics.translate(position.x, position.y);
      graphics.noFill();
      graphics.stroke(255, 0, 0);
      graphics.strokeWeight(5);
      graphics.ellipse(0, 0, targetRadius * 2, targetRadius * 2);
      graphics.popMatrix();
    }
  }

  public void render() {
    mapping.render();
  }

  public void toggleEdit() {
    if (mode != Mode.EDIT) {
      mode = Mode.EDIT;
    } else {
      mode = Mode.RENDER;
    }
  }

  public void mouseDown(float x, float y) {
    if (mode == Mode.EDIT) {
      mapping.mouseDown(x, y);
    } else {
      lastMouse = new PVector(x, y);
      if (lastMouse.dist(mapping.getPosition()) < targetRadius + 10) {
        if (lastMouse.dist(mapping.getPosition()) > targetRadius - 10) {
          mode = Mode.RESIZE;
        } else {
          mode = Mode.MOVE;
        }
      }
    }
  }

  public void mouseUp(float x, float y) {
    if (mode == Mode.EDIT) {
      mapping.mouseUp(x, y);
    } else {
      mode = Mode.RENDER;
    }
  }

  public void mouseMoved(float x, float y) {
    if (mode == Mode.EDIT) {
      mapping.mouseMoved(x, y);
    } else {
      PVector mouse = new PVector(x, y);
      if (mode == Mode.RESIZE) {
        resize(mouse.dist(mapping.getPosition()));
      } else if (mode == Mode.MOVE) {
        setPosition(PVector.add(mapping.getPosition(), PVector.sub(mouse, lastMouse)));
      }

      lastMouse = mouse;
    }
  }
}
