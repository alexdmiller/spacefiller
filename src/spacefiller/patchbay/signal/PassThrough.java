package spacefiller.patchbay.signal;

/**
 * <p>
 * Does not transform the signal, passes it straight through to the output.
 */
public class PassThrough extends Node {
  private int channel = -1;

  public PassThrough setChannel(int channel) {
    this.channel = channel;
    return this;
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    if (channel > 0) {
      setDownstream(values[channel], normalized);
    } else {
      setDownstream(values, true);
    }
  }

  @Override
  public void setValue(int[] values) {
    if (channel > 0) {
      setDownstream(values[channel]);
    } else {
      setDownstream(values);
    }
  }

  @Override
  public void setValue(boolean[] values) {
    if (channel > 0) {
      setDownstream(values[channel]);
    } else {
      setDownstream(values);
    }
  }
}
