package lusio;

import themidibus.MidiBus;
import toxi.geom.Quaternion;

/**
 * Created by miller on 8/12/17.
 */
public class MidiLightcube extends Lightcube {
  private MidiBus bus;
  private int[] values;
  int[] color1 = {255, 255, 0};
  int[] color2 = {0, 255, 255};

  public MidiLightcube() {
    bus = new MidiBus(this, "Launch Control XL 8", 1);
    values = new int[3];
  }

  public void noteOn(int channel, int pitch, int velocity) {
    transitionScene = true;
  }

  public void controllerChange(int channel, int number, int value) {
    previousQuaternion = quaternion;

    if (number == 13) {
      values[0] = value;
    } else if (number == 14) {
      values[1] = value;
    } else if (number == 15) {
      values[2] = value;
    }

    quaternion = Quaternion.createFromEuler(
        (float) (values[0] / 128f * Math.PI * 2),
        (float) (values[1] / 128f * Math.PI * 2),
        (float) (values[2] / 128f * Math.PI * 2));

    rotationalVelocity = Math.max(quaternion.sub(previousQuaternion).magnitude() * 500, rotationalVelocity);

    // get color from data packet
    color = Lusio.instance.color(
        color1[0] + (color2[0] - color1[0]) * getFlipAmount(),
        color1[1] + (color2[1] - color1[1]) * getFlipAmount(),
        color1[2] + (color2[2] - color1[2]) * getFlipAmount());
  }
}
