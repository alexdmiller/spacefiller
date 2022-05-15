package spacefiller.patchbay.signal;

/**
 * <p>
 * Decays the signal by {@code decayAmount} to zero over time. Useful for
 * impulse-like signals (e.g. MIDI note events).
 */
public class Decay extends Node implements SignalUpdater.Updateable {
  private float decayAmount;
  private boolean normalized = true;
  private float currentValues[];
  private float targetValues[];

  public Decay(float decayAmount) {
    this.decayAmount = decayAmount;
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
        if (currentValues[i] > 0) {
          currentValues[i] -= decayAmount;
        }
        currentValues[i] = Math.max(currentValues[i], targetValues[i]);
      }
      setDownstream(currentValues, normalized);
    }
  }

}
