package influencer;

import processing.core.PGraphics;
import sketches.Scene;
import themidibus.MidiBus;
import themidibus.MidiListener;

public class Rain extends Scene implements MidiListener {
  private float fallSpeed = 0.1f;
  private float crossHairSize = 10;

  public static void main(String[] args) {
    main("influencer.Rain");
  }

  MidiBus myBus;
  float[] notes;

  int rows = 5;
  int cols = 20;

  @Override
  protected void doSetup() {
    //set2D();
    println(MidiBus.availableInputs());

    myBus = new MidiBus(this, "UM-ONE", "UM-ONE");
    notes = new float[127];
  }

  public void noteOn(int channel, int pitch, int velocity) {
    notes[pitch % notes.length] = 1;
  }

  public void noteOff(int channel, int pitch, int velocity) {
  //  notes[pitch % notes.length] = 0;
  }

  @Override
  protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
    //background(0);
    //graphics.camera(width/2f , height/2f, (height/2f) / tan((float) (Math.PI*30.0 / 180.0)), width/2f, height/2f, 0, 0, 1, 0);

//    stroke(255);


//
//
//    stroke(100);
//    strokeWeight(2);
//    for (int x = 0; x <= cols; x++) {
//      line(x * cellSize, 0, x * cellSize, rows * cellSize);
//    }
//
//    for (int y = 0; y <= rows; y++) {
//      line(0, y * cellSize, cols * cellSize, y * cellSize);
//    }

//    stroke(255);
//    strokeWeight(2);
//    fill(255);
//
    graphics.pushMatrix();
    //graphics.rotateY(sin(frameCount / 100f) * 0.5f);
    graphics.translate(sin(frameCount / 100f) * 100, 0);
    graphics.translate(-WIDTH/2f, -HEIGHT/2);


    for (int i = 0; i < notes.length; i++) {
      int c = (i * 17) % (cols*rows);
      int x = c % cols;
      int y = c / cols;

      graphics.pushMatrix();
      graphics.noFill();
      graphics.translate(x * ((float)WIDTH/cols), y * ((float)HEIGHT/rows), -200);
      graphics.strokeWeight(1);
      graphics.line(-crossHairSize, 0, crossHairSize, 0);
      graphics.line(0, -crossHairSize, 0, crossHairSize);

      graphics.popMatrix();

      graphics.strokeWeight(3);
      if (notes[i] > 0) {
        graphics.fill(255 * notes[i]);
        graphics.pushMatrix();
        graphics.translate(x * ((float)WIDTH/cols), y * ((float)HEIGHT/rows) - notes[i] * 100, -500);
        graphics.rect(0, 0, ((float)WIDTH/cols), ((float)HEIGHT/rows));
        graphics.popMatrix();
        notes[i] -= fallSpeed;
      }
    }
    graphics.popMatrix();

    //camera();
  }
}
