package spacefiller.patchbay.signal;

/**
 * <p>
 * Smooths the input signal over time. The output signal will converge to the
 * input signal over time. The smoothing value is somewhat unintuitive. A lower
 * value results in a more slowly converging (or smoother) output.
 */
public class Smooth extends Node implements SignalUpdater.Updateable {
  private float smoothAmount;
  private boolean normalized = true;
  private float targetValues[];
  private float currentValues[];


  public Smooth(float smoothAmount) {
    this.smoothAmount = smoothAmount;
    SignalUpdater.getInstance().register(this);
  }

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    currentValues = new float[channelHint];
    targetValues = new float[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    targetValues = values;
    currentValues = Utils.initFloats(currentValues, values.length);

    this.normalized = normalized;
  }

  @Override
  public void setValue(int[] values) {
    targetValues = Utils.initFloats(targetValues, values.length);
    currentValues = Utils.initFloats(currentValues, values.length);

    for (int i = 0; i < values.length; i++) {
      targetValues[i] = values[i];
    }
    this.normalized = false;
  }

  @Override
  public void setValue(boolean[] values) {
    targetValues = Utils.initFloats(targetValues, values.length);
    currentValues = Utils.initFloats(currentValues, values.length);

    for (int i = 0; i < values.length; i++) {
      targetValues[i] = values[i] ? 1f : 0f;
    }
    this.normalized = true;
  }

  @Override
  public void update(float dt) {
    // TODO: factor in dt here
    if (currentValues != null) {
      for (int i = 0; i < currentValues.length; i++) {
        float delta = targetValues[i] - currentValues[i];
        currentValues[i] += delta * smoothAmount;
      }

      if (currentValues.length > 0) {
        setDownstream(currentValues, normalized);
      }
    }
  }
}
