package spacefiller.patchbay.signal;

/**
 * <p>
 * For multi-channel signals (signals that consist of an array of values),
 * outputs the sum of all the incoming signals.
 */
public class Sum extends Node {
  @Override
  public void setValue(float[] values, boolean normalized) {
    float sum = 0;
    for (int i = 0; i < values.length; i++) {
      sum += values[i];
    }

    setDownstream(sum, false);
  }

  @Override
  public void setValue(int[] values) {
    int sum = 0;
    for (int i = 0; i < values.length; i++) {
      sum += values[i];
    }

    setDownstream(sum);
  }

  @Override
  public void setValue(boolean[] values) {
    boolean sum = false;
    for (int i = 0; i < values.length; i++) {
      sum |= values[i];
    }

    setDownstream(sum);
  }
}
