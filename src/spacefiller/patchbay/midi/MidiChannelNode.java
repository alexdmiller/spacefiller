package spacefiller.patchbay.midi;

import spacefiller.patchbay.signal.BooleanNode;

public class MidiChannelNode extends BooleanNode {
  public MidiChannelNode() {
    super();
    setChannelHint(127);
  }

  public void noteOn(int note) {
    boolean[] notes = getArray();
    notes[note] = true;
    setValue(notes);
  }

  public void noteOff(int note) {
    boolean[] notes = getArray();
    notes[note] = false;
    setValue(notes);
  }
}