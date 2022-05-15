package spacefiller.patchbay.signal;

/**
 * <p>
 * Converts the signal to a boolean (true or false) signal. A float signal
 * is converted to a true/false output by testing to see if the input is above
 * 0.5. An integer signal is converted by testing to see if the input is equal
 * to 1.
 * <p>
 * This node, along with {@link FloatNode} and {@link IntNode}, is an output
 * node that allows you to retrieve the signal value. Call the
 * {@link Node#toBoolean()} method on a node to chain a {@code BooleanNode} to
 * the end:
<pre>
import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.signal.BooleanNode;

BooleanNode booleanNode;

void setup() {
  Patchbay patchbay = new Patchbay();
  booleanNode = patchbay.osc().address("/input").toBoolean();
}

void draw() {
  boolean currentValue = booleanNode.get();
}
</pre>
 */
public class BooleanNode extends Node {
  private boolean[] lastValues;

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastValues = new boolean[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    if (normalized) {
      lastValues = Utils.initBooleans(lastValues, values.length);
      for (int i = 0; i < values.length; i++) {
        lastValues[i] = values[i] > 0.5;
      }
    } else {
      throw new UnsupportedOperationException("Trying to send denormalized float value to boolean data receiver.");
    }

    setDownstream(lastValues);
  }

  @Override
  public void setValue(int[] values) {
    lastValues = Utils.initBooleans(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i] == 1;
    }
    setDownstream(lastValues);
  }

  @Override
  public void setValue(boolean[] values) {
    lastValues = values;
    setDownstream(lastValues);
  }

  public boolean[] getArray() {
    return lastValues;
  }

  public boolean get(int index) {
    return lastValues[index];
  }

  public boolean get() {
    return (lastValues == null || lastValues.length == 0) ? false : lastValues[0];
  }
}