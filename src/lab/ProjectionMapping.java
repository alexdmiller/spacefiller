package lab;

import mapping.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PJOGL;
import toxi.color.TColor;

/**
 * Created by miller on 11/11/17.
 */
public class ProjectionMapping extends PApplet {
  private static final int MAPPED_WIDTH = 800;
  private static final int MAPPED_HEIGHT = 800;
  private boolean showUI;

  public static void main(String[] args) {
    main("lab.ProjectionMapping");
  }

  private CircleMapping mapping;
  private PGraphics canvas;

  public void settings() {
    fullScreen(P3D, 2);
    //size(1000, 1000, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    canvas = createGraphics(MAPPED_WIDTH, MAPPED_HEIGHT, P3D);

    canvas.beginDraw();
    canvas.background(0);
    canvas.stroke(255);
    canvas.ellipse(MAPPED_WIDTH/2, MAPPED_HEIGHT/2, MAPPED_WIDTH, MAPPED_HEIGHT);
    canvas.endDraw();

    mapping = new CircleMapping(getGraphics(), canvas, MAPPED_WIDTH / 2);
    mapping.setDivisions(15);

  }

  private float t = 0;

  public void draw() {
    t += 1;

    background(0);

    canvas.beginDraw();
    canvas.background(0);
    canvas.background(0);
    canvas.translate(MAPPED_WIDTH / 2, MAPPED_HEIGHT / 2);
    canvas.noFill();
    canvas.stroke(255);
    canvas.strokeWeight(2);

    for (int i = 0; i < 10; i++) {
      float radius = (MAPPED_WIDTH / 10 * i + t) % MAPPED_WIDTH;

      canvas.ellipse(0, 0, radius * 2, radius * 2);
    }

//    PShape tools = mapping.getSourceShape();
//
//    canvas.beginShape(TRIANGLE_FAN);
//    canvas.noStroke();
//
//    TColor rotatedRYB = TColor.BLUE.getRotatedRYB(t / 100f);
//
//    for (int i = 0; i < tools.getVertexCount(); i++) {
//      canvas.fill((pow(sin(i / 10f + t / 100) + 1 / 2, 10)) * 255);
//      canvas.vertex(tools.getVertexX(i), tools.getVertexY(i));
//    }
//    canvas.endShape();
    canvas.endDraw();

    mapping.render();

    if (showUI) {
      mapping.renderUI();
    }
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

  public void keyPressed() {
    if (key == 'e') {
      mapping.toggleEdit();
    }

    if (key == 'h') {
      showUI = !showUI;
    }
  }
}
