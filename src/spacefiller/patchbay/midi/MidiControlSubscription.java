package spacefiller.patchbay.midi;

public class MidiControlSubscription {
  private String device = null;
  private int channel = -1;
  private int control = -1;

  public boolean matches(String controllerName, int channel, int control) {
    boolean controllerMatches =
        this.device == controllerName ||
            this.device == null;
    boolean channelMatches =
        this.channel == -1 || this.channel == channel;
    boolean controlMatches =
        this.control == -1 || this.control == control;
    return controllerMatches && channelMatches && controlMatches;
  }

//  public boolean equals(Object object) {
//    if (this == object) return true;
//    if (object == null || getClass() != object.getClass()) return false;
//    if (!super.equals(object)) return false;
//    MidiControlSubscription that = (MidiControlSubscription) object;
//    return channel == that.channel &&
//        control == that.control &&
//        java.util.Objects.equals(device, that.device);
//  }
//
//  public int hashCode() {
//    return Objects.hash(super.hashCode(), device, channel, control);
//  }

  public void setDevice(String device) {
    this.device = device;
  }

  public void setChannel(int channel) {
    this.channel = channel;
  }

  public void setControl(int control) {
    this.control = control;
  }

  public String getDevice() {
    return device;
  }

  public int getChannel() {
    return channel;
  }

  public int getControl() {
    return control;
  }
}
