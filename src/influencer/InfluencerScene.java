package influencer;

import spacefiller.remote.MidiRemoteControl;
import spacefiller.remote.signal.FloatNode;
import spacefiller.remote.signal.Node;

public class InfluencerScene extends Scene {
  protected MidiRemoteControl control = midi("Launch Control XL 8");
  protected MidiRemoteControl music = midi("Bus 1");

  protected FloatNode mainSlider = control.controller(13).toFloat();

  protected Node notes = music.notes(0);
  protected Node drums = music.notes(1);

  protected FloatNode decayedDrums = drums.impulse(1f).decay(0.1f).toFloat();
  protected FloatNode decayedNotes = notes.impulse(1f).decay(0.05f).toFloat();

  protected Node kick = drums.channel(60);
  protected Node snare = drums.channel(61);

  protected FloatNode kickDecay = kick.impulse(1f).decay(0.02f).toFloat();
  protected FloatNode snareDecay = snare.impulse(1f).decay(0.02f).toFloat();
}
