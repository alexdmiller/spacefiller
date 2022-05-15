package spacefiller.patchbay.signal;

/**
 * <p>
 * For multi-channel signals (signals that consist of an array of values),
 * outputs the max of all the incoming signals.
 */
public class Max extends Node {
  @Override
  public void setValue(float[] values, boolean normalized) {
    float max = 0;
    for (int i = 0; i < values.length; i++) {
      max = Math.max(max, values[i]);
    }

    setDownstream(max, false);
  }

  @Override
  public void setValue(int[] values) {
    int max = 0;
    for (int i = 0; i < values.length; i++) {
      max = Math.max(max, values[i]);
    }

    setDownstream(max);
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
