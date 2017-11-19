package lab;

import mapping.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PJOGL;

/**
 * Created by miller on 11/11/17.
 */
public class ProjectionMapping extends PApplet {
  private static final int MAPPED_WIDTH = 800;
  private static final int MAPPED_HEIGHT = 800;

  public static void main(String[] args) {
    main("lab.ProjectionMapping");
  }

  private CircleMapping mapping;
  private PGraphics canvas;
  private PShape postShape;

  public void settings() {
    //fullScreen(P3D, 2);
    size(1000, 1000, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
//    PShape shape = createShape();
//
//    shape.beginShape(TRIANGLE_FAN);
//    shape.vertex(MAPPED_WIDTH / 2, MAPPED_HEIGHT / 2);
//    for (float theta = 0; theta <= PI * 2; theta += PI / 10f) {
//      shape.vertex(
//          MAPPED_WIDTH / 2f + cos(theta) * MAPPED_WIDTH / 2f,
//          MAPPED_HEIGHT / 2f + sin(theta) * MAPPED_WIDTH / 2f);
//    }
//    shape.endShape();
//
    canvas = createGraphics(MAPPED_WIDTH, MAPPED_HEIGHT, P3D);

    canvas.beginDraw();
    canvas.background(0);
    canvas.stroke(255);
    canvas.ellipse(MAPPED_WIDTH / 2, MAPPED_HEIGHT / 2, 500, 500);
    canvas.endDraw();
//
//    mapper = new ShapeMapping(getGraphics(), shape, canvas);

    mapping = new CircleMapping(getGraphics(), canvas);
  }

  private float t = 0;

  public void draw() {

    background(0);

    mapping.render();
    mapping.renderUI();
//    t += 1;
//
//    background(0);
//
//    canvas.beginDraw();
//    canvas.background(0);
//    canvas.translate(MAPPED_WIDTH / 2, MAPPED_HEIGHT / 2);
//    canvas.noFill();
//    canvas.stroke(255);
//    canvas.strokeWeight(2);
//
//    for (int i = 0; i < 10; i++) {
//      float radius = (MAPPED_WIDTH / 10 * i + t) % MAPPED_WIDTH;
//
//      canvas.ellipse(0, 0, radius * 2, radius * 2);
//    }
//
//    canvas.endDraw();
//
//    mapper.render();
//    mapper.renderUI();
  }

  @Override
  public void mousePressed() {
    mapping.mouseDown(mouseX, mouseY);
  }

  @Override
  public void mouseReleased() {
    mapping.mouseUp(mouseX, mouseY);
  }

  @Override
  public void mouseDragged() {
    mapping.mouseMoved(mouseX, mouseY);
  }
}
