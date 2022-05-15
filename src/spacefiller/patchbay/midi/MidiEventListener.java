package spacefiller.patchbay.midi;

public interface MidiEventListener {
  void noteOn(String device, int channel, int note, int velocity);
  void noteOff(String device, int channel, int note);
}
