package algoplex2;

import themidibus.MidiBus;
import themidibus.SimpleMidiListener;

import java.util.Arrays;

/**
 * Created by miller on 8/30/17.
 */
public class Controller implements SimpleMidiListener {
  public static final int NUM_VALUES = 6;

  private float values[];
  private MidiBus bus;

  public Controller() {
    this.values = new float[NUM_VALUES];
    Arrays.fill(values, 0.2f);
    this.bus = new MidiBus(this, "Launch Control XL 8", 1);
  }

  public float getValue(int index) {
    return values[index];
  }

  @Override
  public void noteOn(int i, int i1, int i2) { }

  @Override
  public void noteOff(int i, int i1, int i2) { }

  @Override
  public void controllerChange(int channel, int number, int value) {
    if (number - 13 < values.length) {
      values[number - 13] = value / 255f;
    }
  }
}
