package influencer;

import themidibus.SimpleMidiListener;

import javax.sound.midi.*;

public class MidiState implements Receiver {
  private MidiDevice device;

  private int channels[][];
  private int notes[];
  private int controllers[][];

  public MidiState(String deviceName) throws MidiUnavailableException {
    MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
    for (MidiDevice.Info info : infos) {
      if (info.getName() == deviceName) {
        MidiDevice candidate = MidiSystem.getMidiDevice(info);
        if (candidate.getMaxReceivers() > 0) {
          device = candidate;
        }
      }
    }

    if (device == null) {
      throw new MidiUnavailableException();
    }

    notes = new int[127];
    channels = new int[127][127];
    controllers = new int[127][127];
  }

  @Override
  public void send(MidiMessage message, long timeStamp) {
    byte[] data = message.getMessage();

    int channel = data[0] & 15;
    int note = data[1] & 255;
    int value = data[2] & 255;

    if ((data[0] & 240) == 144) {
      // note on
      channels[channel][note] = value;
      notes[note] = value;
    } else if ((data[0] & 240) == 128) {
      // note off
      channels[channel][note] = 0;
      notes[note] = 0;
    } else if ((data[0] & 240) == 176) {
      // controller change
      controllers[channel][note] = value;
    }
  }

  @Override
  public void close() {

  }
}
