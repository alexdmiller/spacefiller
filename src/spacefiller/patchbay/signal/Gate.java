package spacefiller.patchbay.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * If the signal is greater than {@code threshold}, outputs a {@code 1},
 * otherwise outputs a {@code 0}.
 * <p>
 * Call the {@link Node#gate(float)} method on a node to chain a {@code Gate}
 * node to the end:
<pre>
Gate node = patchbay.osc().address("/input").gate(0.7);
</pre>
 */
public class Gate extends Node {
  private float threshold;
  private float[] lastValues;
  private List<GateTriggerListener> listeners;

  private int debounceCount = 0;
  private int debounceThreshold = 0;

  public Gate(float threshold) {
    this.threshold = threshold;
    listeners = new ArrayList<>();
  }

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastValues = new float[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    lastValues = Utils.initFloats(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      float last = lastValues[i];
      lastValues[i] = values[i];
      if (last < threshold && values[i] > threshold && debounceCount == 0) {
        if (debounceCount == 0) {
          // does this work?
          values[i] = 1f;
          notifyListeners();
        }

        debounceCount = debounceThreshold;
      } else {
        values[i] = 0f;

        if (debounceCount > 0) {
          debounceCount--;
        }
      }
    }

    setDownstream(values, true);
  }

  @Override
  public void setValue(int[] values) {
    lastValues = Utils.initFloats(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      float last = lastValues[i];
      lastValues[i] = values[i];
      if (last < threshold && values[i] > threshold && debounceCount == 0) {
        if (debounceCount == 0) {
          // does this work?
          values[i] = 1;
          notifyListeners();
        }

        debounceCount = debounceThreshold;
      } else {
        values[i] = 0;

        if (debounceCount > 0) {
          debounceCount--;
        }
      }
    }

    setDownstream(values);
  }

  private void notifyListeners() {
    for (GateTriggerListener listener : listeners) {
      listener.onGateTriggered();
    }
  }

  @Override
  public void setValue(boolean[] values) {
    // TODO: this should be implementable
    throw new UnsupportedOperationException();
  }

  public void onGateTriggered(GateTriggerListener listener) {
    listeners.add(listener);
  }


  protected interface GateTriggerListener {
    void onGateTriggered();
  }

  public void debounce(int threshold) {
    debounceThreshold = threshold;
  }
}
