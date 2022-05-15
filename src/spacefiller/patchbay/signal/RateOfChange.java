package spacefiller.patchbay.signal;

/**
 * Outputs the frame-to-frame delta of the signal.
 */
public class RateOfChange extends Node {
  private boolean normalized = true;
  private float lastValues[];

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastValues = new float[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    lastValues = Utils.initFloats(lastValues, values.length);

    float[] delta = new float[values.length];

    for (int i = 0; i < values.length; i++) {
      delta[i] = values[i] - lastValues[i];
    }
    this.normalized = normalized;
    setDownstream(delta, normalized);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i];
    }
  }

  @Override
  public void setValue(int[] values) {
    lastValues = Utils.initFloats(lastValues, values.length);

    float[] delta = new float[values.length];

    for (int i = 0; i < values.length; i++) {
      delta[i] = values[i] - lastValues[i];
    }
    this.normalized = normalized;
    setDownstream(delta, normalized);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i];
    }
  }

  @Override
  public void setValue(boolean[] values) {
    throw new UnsupportedOperationException();
  }
}