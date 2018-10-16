package influencer;

import spacefiller.remote.MidiRemoteControl;
import spacefiller.remote.signal.FloatDataReceiver;
import themidibus.MidiListener;

public class Vertical extends Scene implements MidiListener {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new Vertical());
  }

  private MidiRemoteControl midi;
  private FloatDataReceiver lineWidth;

  @Override
  public void setup() {
    midi = midi("Launch Control XL 8");

    lineWidth = midi.controller(16).smooth(0.1f).scale(1, 100).toFloat();
  }

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

    // TODO: figure out how to get lists of notes from midi controller
//    for (int i = 0; i < notes.length; i++) {
//      float x = (float) (i - 30) / notes.length * width;
//      if (notes[i] > 0) {
//        stroke(255 * notes[i]);
//        line(x, 0, x, height);
//        notes[i] -= fallSpeed;
//      }
//    }
  }
}
