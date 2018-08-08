package tools;

import boids.emitter.PointEmitter;
import particles.Bounds;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import sketches.SceneTool;

import java.util.List;

import static processing.core.PConstants.RECT;

public class ShapeEditor extends  SceneTool {
  private List<PShape> shapes;
  private PShape currentShape = null;
  private int selectedVertexIndex = -1;
  private PApplet parent;


  public ShapeEditor(List<PShape> shapes, PApplet parent) {
    this.shapes = shapes;
    this.parent = parent;
  }

  @Override
  public void mousePressed(float mouseX, float mouseY) {
    PVector mouse = new PVector(mouseX, mouseY);
    for (PShape shape : shapes) {
      for (int i = 0; i < shape.getVertexCount(); i++) {
        System.out.println(shape.getVertex(i).dist(mouse));
        if (shape.getVertex(i).dist(mouse) < 20) {
          currentShape = shape;
          selectedVertexIndex = i;
          return;
        }
      }
    }
  }

  public void mouseDragged(float mouseX, float mouseY) {
    if (currentShape != null) {
      currentShape.setVertex(selectedVertexIndex, mouseX, mouseY);
    }
  }


  @Override
  public void keyDown(char key) {
    if (key == 'n') {
      PShape shape = parent.createShape();
      shape.beginShape();
      shape.vertex(0, 0);
      shape.vertex(0, 100);
      shape.vertex(100, 100);
      shape.vertex(100, 0);
      shape.endShape();

      shape.setStroke(parent.color(255, 255, 255));
      shape.setFill(parent.color(100, 100, 100));
      shapes.add(shape);
    }
  }


  @Override
  public void render(PGraphics graphics, float mouseX, float mouseY, boolean mousePressed) {
    for (int i = 0; i < shapes.size(); i++) {
      //graphics.shape(shapes.get(i), 0, 0);
    }
  }

  @Override
  public String toString() {
    return "SHAPE";
  }

  @Override
  public void clear() {
    shapes.clear();
  }
}
