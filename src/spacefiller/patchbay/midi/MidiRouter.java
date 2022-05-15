package spacefiller.patchbay.midi;

import spacefiller.patchbay.signal.Node;
import spacefiller.patchbay.signal.PassThrough;

import javax.sound.midi.*;
import java.util.*;

// MidiEventBus stores mapping from control identifiers (controller indices and channel indices)
// to

public class MidiRouter {
  private boolean logEvents = false;

  // subscribers for control events
  private Map<MidiControlSubscription, Node> controlSubscribers;

  // subscribers to normal note events — each subscriber listens for a single note
  private Map<MidiNoteSubscription, Node> noteSubscribers;

  // a separate map to hold a special case: subscribers that listen for all the notes in
  // a channel. nodes that subscribe to this type of event need to be handled differently,
  // so they are represented by a special node (MidiChannelNode) and stores separately.
  private Map<MidiNoteSubscription, MidiChannelNode> channelSubscribers;

  private List<MidiEventListener> eventListeners;

  private Queue<DeviceMidiMessage> messageQueue;

  private boolean sync = false;

  public MidiRouter() {
    controlSubscribers = new HashMap<>();
    noteSubscribers = new HashMap<>();
    channelSubscribers = new HashMap<>();
    eventListeners = new ArrayList<>();
    messageQueue = new LinkedList<>();

    new RegisterMidiDevices().start();
  }

  public void update() {
    while (!messageQueue.isEmpty()) {
      DeviceMidiMessage message = messageQueue.poll();
      processMidiMessage(message);
    }
  }

  public void syncOn() {
    sync = true;
  }

  public void syncOff() {
    sync = false;
  }

  public Map<MidiControlSubscription, Node> getControlSubscribers() {
    return controlSubscribers;
  }

  public Map<MidiNoteSubscription, Node> getNoteSubscribers() {
    return noteSubscribers;
  }

  public Map<MidiNoteSubscription, MidiChannelNode> getChannelSubscribers() {
    return channelSubscribers;
  }

  public void registerEventListener(MidiEventListener eventListener) {
    eventListeners.add(eventListener);
  }

  public class RegisterMidiDevices extends Thread {
    public void run(){
      for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
        try {
          MidiDevice device = MidiSystem.getMidiDevice(info);
          // TODO: does this handle midi devices with the same name?
          String deviceName = info.getName();

          // a device with at least one transmitter has midi input
          if (device.getMaxTransmitters() != 0) {
            device.open();

            Transmitter transmitter = device.getTransmitter();
            Receiver receiver = new MidiReceiver(deviceName);

            transmitter.setReceiver(receiver);
          }

        } catch (MidiUnavailableException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void log() {
    logEvents = true;
  }

  public void noLog() {
    logEvents = false;
  }

  public Node control(int control) {
    return control(null, -1, control);
  }

  public Node control(int channel, int control) {
    return control(null, channel, control);
  }

  public Node control(String device, int channel, int control) {
    MidiControlSubscription subscription = new MidiControlSubscription();
    subscription.setDevice(device);
    subscription.setChannel(channel);
    subscription.setControl(control);
    return subscribe(subscription);
  }

  public Node note(int note) {
    return note(null, -1, note);
  }

  public Node note(int channel, int note) {
    return note(null, channel, note);
  }

  public Node note(String device, int channel, int note) {
    MidiNoteSubscription subscription = new MidiNoteSubscription();
    subscription.setDevice(device);
    subscription.setChannel(channel);
    subscription.setNote(note);
    return subscribe(subscription);
  }

  public Node notes(int channel) {
    return notes(null, channel);
  }

  public Node notes(String device, int channel) {
    MidiNoteSubscription subscription = new MidiNoteSubscription();
    subscription.setDevice(device);
    subscription.setChannel(channel);

    if (!channelSubscribers.containsKey(subscription)) {
      channelSubscribers.put(subscription, new MidiChannelNode());
    }

    return channelSubscribers.get(subscription);
  }

  protected void publishNoteOn(String device, int channel, int note, int velocity) {
    channelSubscribers.keySet().forEach(subscription -> {
      if (subscription.matches(device, channel, note)) {
        channelSubscribers.get(subscription).noteOn(note);
      }
    });

    noteSubscribers.keySet().forEach(subscription -> {
      if (subscription.matches(device, channel, note)) {
        noteSubscribers.get(subscription).setValue(velocity / 127f, true);
      }
    });

    eventListeners.forEach(eventListener -> {
      eventListener.noteOn(device, channel, note, velocity);
    });
  }

  protected void publishNoteOff(String device, int channel, int note) {
    channelSubscribers.keySet().forEach(subscriber -> {
      if (subscriber.matches(device, channel, note)) {
        channelSubscribers.get(subscriber).noteOff(note);
      }
    });

    noteSubscribers.keySet().forEach(subscription -> {
      if (subscription.matches(device, channel, note)) {
        noteSubscribers.get(subscription).setValue(0, true);
      }
    });

    eventListeners.forEach(eventListener -> {
      eventListener.noteOff(device, channel, note);
    });
  }

  protected void publishControlEvent(String device, int channel, int control, int value) {
    controlSubscribers.keySet().forEach(subscriber -> {
      if (subscriber.matches(device, channel, control)) {
        controlSubscribers.get(subscriber).setValue(value / 127f, true);
      }
    });
  }

  protected Node subscribe(MidiControlSubscription subscription) {
    if (!controlSubscribers.containsKey(subscription)) {
      controlSubscribers.put(subscription, new PassThrough());
    }

    return controlSubscribers.get(subscription);
  }

  protected Node subscribe(MidiNoteSubscription subscription) {
    if (!noteSubscribers.containsKey(subscription)) {
      noteSubscribers.put(subscription, new PassThrough());
    }

    return noteSubscribers.get(subscription);
  }

  private void processMidiMessage(DeviceMidiMessage deviceMidiMessage) {
    MidiMessage message = deviceMidiMessage.midiMessage;
    String device = deviceMidiMessage.device;

    byte[] data = message.getMessage();

    int channel = (int)(data[0] & 0x0F);
    int data1 = (int)(data[1] & 0xFF);
    int data2 = (data[2] & 0xFF);

    if((int)((byte)data[0] & 0xF0) == ShortMessage.NOTE_ON) {
      if (logEvents) System.out.println("note on: device = " + device + ", channel = " + channel + ", note = " + data1 + ", velocity = " + data2);
      // Interpret a velocity of zero to mean the note is off
      if (data2 == 0) {
        publishNoteOff(device, channel, data1);
      } else {
        publishNoteOn(device, channel, data1, data2);
      }
    } else if((int)((byte)data[0] & 0xF0) == ShortMessage.NOTE_OFF) {
      if (logEvents) System.out.println("note off: device = " + device + ", channel = " + channel + ", note = " + data1 + ", velocity = " + data2);
      publishNoteOff(device, channel, data1);
    } else if((int)((byte)data[0] & 0xF0) == ShortMessage.CONTROL_CHANGE) {
      if (logEvents) System.out.println("control change: device = " + device + ", channel = " + channel + ", control = " + data1 + ", value = " + data2);
      publishControlEvent(device, channel, data1, data2);
    }
  }

  private class MidiReceiver implements Receiver {
    private String device;

    public MidiReceiver(String device) {
      this.device = device;
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
      DeviceMidiMessage deviceMidiMessage = new DeviceMidiMessage(message, device);
      if (sync) {
        messageQueue.add(deviceMidiMessage);
      } else {
        processMidiMessage(deviceMidiMessage);
      }
    }

    @Override
    public void close() {

    }
  }

}
