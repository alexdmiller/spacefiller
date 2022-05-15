package spacefiller.patchbay.midi;

import javax.sound.midi.MidiMessage;

public class DeviceMidiMessage {
  MidiMessage midiMessage;
  String device;

  public DeviceMidiMessage(MidiMessage midiMessage, String device) {
    this.midiMessage = midiMessage;
    this.device = device;
  }
}
