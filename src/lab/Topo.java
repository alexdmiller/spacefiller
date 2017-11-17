package lab;

import mapping.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PJOGL;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;

import java.util.*;

/**
 * Created by miller on 11/11/17.
 */
public class Topo extends PApplet {
  public static void main(String[] args) {
    main("lab.Topo");
  }

  private Keystone ks;
  private List<AdjustableContour> contours;
  private float t;

  public void settings() {
    fullScreen(P3D, 2);
    PJOGL.profile = 1;
  }

  public void setup() {
    contours = new ArrayList<AdjustableContour>();
    ks = new Keystone(this);
    PShape shape = loadShape("contours_modified.svg");

    int i = 0;
    for (PShape child : shape.getChildren()) {
      contours.add(new AdjustableContour(createGraphics(500, 500, P3D), child, ks.createCornerPinSurface(500, 500, 10), i));
      i++;
    }

    Collections.reverse(contours);

    ks.toggleCalibration();
    ks.load();
  }

  public void draw() {
    if (!ks.isCalibrating()) {
      t += 0.1f;
    }
    background(0);

    for (AdjustableContour contour : contours) {
      contour.draw();
    }
  }

  class AdjustableContour {
    PGraphics graphics;
    PShape shape;
    CornerPinSurface surface;
    int i;

    public AdjustableContour(PGraphics graphics, PShape shape, CornerPinSurface surface, int i) {
      this.graphics = graphics;
      this.shape = shape;
      this.surface = surface;
      this.i = i;
    }

    void draw() {
      shape.disableStyle();

      ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(i / 10f + t / 5f);

      if (i % 2 == 0) {
        color1 = color1.getRotatedRYB(30);
      }

      graphics.beginDraw();
      graphics.clear();
      graphics.fill(color1.toARGB());
//      graphics.noFill();
//      graphics.stroke(255);
//      graphics.strokeWeight(sin(i  + t) * 5 + 5);
      graphics.shape(this.shape);
      graphics.endDraw();

      surface.render(graphics);
    }
  }

  public void keyPressed() {
    if (key == ' ') {
      ks.toggleCalibration();
    } else if (keyCode == RIGHT) {
      ks.nextSurface();
    } else if (key == 's') {
      ks.save();
    }
  }
}
