package influencer;

import spacefiller.remote.signal.FloatNode;
import themidibus.MidiListener;

public class Vertical extends InfluencerScene implements MidiListener {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new Vertical());
  }

  private FloatNode lineWidth = control.controller(16).smooth(0.1f).scale(1, 100).toFloat();

  @Override
  public void draw() {
    background(0);
    noFill();

    stroke(100);
    strokeWeight(lineWidth.get());

    for (int y = 0; y < 10; y++) {
      float h = (y * height / 10f + frameCount) % height;
      line(0, h, width, h);
    }

    stroke(255);
    strokeWeight(5);

    float[] notes = decayedNotes.getArray();
    for (int i = 0; i < notes.length; i++) {
      float x = (float) i * width / notes.length;
      if (notes[i] > 0) {
        stroke(255 * notes[i]);
        line(x, 0, x, height);
      }
    }
  }
}
