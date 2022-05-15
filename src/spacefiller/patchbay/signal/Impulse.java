package spacefiller.patchbay.signal;
import java.util.Arrays;

/**
 * <p>
 * If the signal is greater than {@code threshold}, outputs a single value
 * of {@code 1} for one frame; otherwise continuously outputs a signal of {@code 0}.
 * Useful for triggering a function call when a float signal passes a threshold.
 * <p>
 * Call the {@link Node#impulse(float)} method on a node to chain an {@code Impulse}
 * node to the end:
<pre>
Impulse node = patchbay.osc().address("/input").impulse(0.7);
</pre>
 */
public class Impulse extends Node implements SignalUpdater.Updateable {
  private float threshold;
  private float[] lastInput;
  private float[] nextOutput;

  public Impulse(float threshold) {
    this.threshold = threshold;
    SignalUpdater.getInstance().register(this);
  }

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastInput = new float[channelHint];
    nextOutput = new float[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    lastInput = Utils.initFloats(lastInput, values.length);
    nextOutput = Utils.initFloats(nextOutput, values.length);

    for (int i = 0; i < values.length; i++) {
      if (lastInput[i] < threshold && values[i] > threshold) {
        nextOutput[i] = 1f;
      }
      lastInput[i] = values[i];
    }
  }

  @Override
  public void setValue(int[] values) {
    lastInput = Utils.initFloats(lastInput, values.length);
    nextOutput = Utils.initFloats(nextOutput, values.length);

    for (int i = 0; i < values.length; i++) {
      if (lastInput[i] < threshold && values[i] > threshold) {
        nextOutput[i] = 1f;
      }
      lastInput[i] = values[i];
    }
  }

  @Override
  public void setValue(boolean[] values) {
    lastInput = Utils.initFloats(lastInput, values.length);
    nextOutput = Utils.initFloats(nextOutput, values.length);

    for (int i = 0; i < values.length; i++) {
      if (lastInput[i] < threshold && values[i]) {
        nextOutput[i] = 1f;
      }
      lastInput[i] = values[i] ? 1 : 0;
    }
  }

  @Override
  public void update(float delta) {
    if (nextOutput != null) {
      setDownstream(nextOutput, true);
      Arrays.fill(nextOutput, 0);
    }
  }
}
