package influencer;

import de.looksgood.ani.Ani;
import processing.core.PGraphics;
import sketches.Scene;
import themidibus.MidiBus;
import themidibus.MidiListener;
import toxi.geom.Quaternion;
import toxi.math.ExponentialInterpolation;
import toxi.math.InterpolateStrategy;

public class CubeGrid extends Scene implements MidiListener {
  private float fadeSpeed = 0.07f;

  public static void main(String[] args) {
    main("influencer.CubeGrid");
  }

  MidiBus myBus;
  float[] notes;
  float[] drums;
  float[] controllers;

  int rows = 10;
  int cols = 10;
  float cellSize = 50;

  private Quaternion orientation = new Quaternion();
  private Quaternion targetOrientation = new Quaternion();
  private float interpolationAmount = 0;
  private InterpolateStrategy interpolateStrategy = new ExponentialInterpolation(0.8f);

  @Override
  protected void doSetup() {
    println(MidiBus.availableInputs());

    myBus = new MidiBus(this, "UM-ONE", "UM-ONE");
    notes = new float[127];
    drums = new float[127];
    controllers = new float[127];

    Ani.init(this);
  }

  public void controllerChange(int channel, int controller, int value) {
    println(controller);
    if (controllers != null) {
      controllers[controller] = value / 127f;
    }
  }


  public void noteOn(int channel, int pitch, int velocity) {
    if (notes != null) {
      if (channel == 0) {
        drums[pitch % notes.length] = 1;
      } else {
        notes[pitch % notes.length] = 1;
      }
    }

  }

  public void noteOff(int channel, int pitch, int velocity) {
//    notes[pitch % notes.length] = false;
  }

  @Override
  protected void doMouseDown(float x, float y) {
    orientation = targetOrientation;
    targetOrientation = Quaternion.createFromEuler(
        0,
        floor(random(4)) / 4f * PI * 2,
        floor(random(4)) / 4f * PI * 2 + PI / 4);

    interpolationAmount = 0;
    Ani.to(this, 1, "interpolationAmount", 1);
  }

  @Override
  protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
    rows = (int) (controllers[48] * 10 + 1);
    cols = (int) (controllers[48] * 10 + 1);

    Quaternion current = orientation;

    if (!orientation.equals(targetOrientation)) {
      if (interpolationAmount < 1) {
        current = orientation.interpolateTo(targetOrientation, interpolationAmount, interpolateStrategy);
      }

      if (interpolationAmount >= 1) {
        interpolationAmount = 0;
        orientation = targetOrientation;
        current = orientation;
      }
    }

    //graphics.camera((cols * cellSize)/2f, (rows*cellSize)/2f, 100, (cols * cellSize)/2f, (rows*cellSize)/2f, 0, 0, 0, -1);
    graphics.translate(-(cols * cellSize)/2f, -(rows*cellSize)/2f);
//    graphics.rotateX((controllers[48]) * PI/10f);
//    graphics.rotateY((controllers[48]) * PI/10f);

    float[] axis = current.toAxisAngle();
    graphics.rotate(axis[0], axis[1], axis[2], axis[3]);
    graphics.translate((cols * cellSize)/2f, (rows*cellSize)/2f);

//
//    graphics.scale(2);
//
    graphics.ortho();

    graphics.background(0);
    graphics.noFill();
    graphics.stroke(255);


//    graphics.translate((cols * cellSize)/2f, (cols * cellSize)/2f);
//    graphics.rotateZ(frameCount / 200f);
//    graphics.translate(-(cols * cellSize)/2f, -(cols * cellSize)/2f);


    graphics.stroke(100);
    graphics.strokeWeight(2);
    graphics.pushMatrix();

    graphics.translate(0, 0, +50 + drums[48] * 30);

    if (drums[50] > 0) {
      graphics.stroke(lerpColor(color(255), color(0, 255, 255), drums[50]));
    } else if (drums[48] > 0) {
      graphics.stroke(lerpColor(color(255), color(255, 0, 255), drums[48]));
    } else {
      graphics.stroke(255);
    }

    for (int x = 0; x <= cols; x++) {
      graphics.line(x * cellSize, 0, x * cellSize, rows * cellSize);
    }

    for (int y = 0; y <= rows; y++) {
      graphics.line(0, y * cellSize, cols * cellSize, y * cellSize);
    }

    graphics.popMatrix();

    graphics.stroke(255);
    graphics.strokeWeight(2);
    graphics.fill(0);

    graphics.translate(0, 0, -100 - drums[48] * 30);

    if (drums[50] > 0) {
      graphics.stroke(lerpColor(color(255), color(0, 255, 255), drums[50]));
    } else if (drums[48] > 0) {
      graphics.stroke(lerpColor(color(255), color(255, 0, 255), drums[48]));
    } else {
      graphics.stroke(255);
    }

    for (int i = 0; i < notes.length; i++) {
      graphics.pushMatrix();

      int c = i % (cols*rows);
      int x = c % cols;
      int y = c / cols;

      graphics.fill(255 * notes[i]);
      float height = notes[i] * notes[i] * cellSize * 2;
      graphics.translate(x * cellSize + cellSize/2, y * cellSize + cellSize/2, height/2);
      graphics.box(cellSize, cellSize, height);
      graphics.popMatrix();

      if (notes[i] > 0) {
        notes[i] -= fadeSpeed;
      }
    }

    for (int i = 0; i < drums.length; i++) {


      if (drums[i] > 0) {
        drums[i] -= fadeSpeed;
      }
    }

    //camera();
  }
}
