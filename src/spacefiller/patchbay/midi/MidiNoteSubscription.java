package spacefiller.patchbay.midi;

public class MidiNoteSubscription {
  private String device = null;
  private int channel = -1;
  private int note = -1;

  public boolean matches(String controllerName, int channel, int note) {
    boolean controllerMatches =
        this.device == controllerName ||
            this.device == null;
    boolean channelMatches =
        this.channel == -1 || this.channel == channel;
    boolean controlMatches =
        this.note == -1 || this.note == note;
    return controllerMatches && channelMatches && controlMatches;
  }

  public void setDevice(String device) {
    this.device = device;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setNote(int note) {
    this.note = note;
  }

  public String getDevice() {
    return device;
  }

  public int getChannel() {
    return channel;
  }

  public int getNote() {
    return note;
  }

  // A channel event is an event that corresponds to *any* note in a channel being fired.
  public boolean isChannelSubscriber() {
    return note == -1;
  }
}
