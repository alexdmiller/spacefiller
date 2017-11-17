package lab;

import mapping.*;
import processing.core.PApplet;
import processing.core.PShape;
import processing.opengl.PJOGL;

/**
 * Created by miller on 11/11/17.
 */
public class ProjectionMapping extends PApplet {
  public static void main(String[] args) {
    main("lab.ProjectionMapping");
  }

  private Mapping mapper;

  public void settings() {
    fullScreen(P3D, 1);
    PJOGL.profile = 1;
  }

  public void setup() {
    PShape shape = createShape();

    shape.beginShape();
    shape.vertex(100, 100);
    shape.vertex(100, 200);
    shape.vertex(200, 200);
    shape.vertex(300, 300);
    shape.vertex(100, 300);
    shape.endShape();

    mapper = new Mapping(getGraphics(), shape);

  }

  public void draw() {
    background(0);
    mapper.renderUI();
  }

  @Override
  public void mousePressed() {
    mapper.mouseDown(mouseX, mouseY);
  }

  @Override
  public void mouseReleased() {
    mapper.mouseUp(mouseX, mouseY);
  }

  @Override
  public void mouseDragged() {
    mapper.mouseMoved(mouseX, mouseY);
  }
}
