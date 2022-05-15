package spacefiller.patchbay.signal;

/**
 * <p>
 * Convert any signal type to an int signal output. {@code true} boolean
 * values will be converted to {@code 1}, and {@code false} boolean values will
 * be converted to {@code 0}.
 * <p>
 * This node, along with {@link FloatNode} and {@link BooleanNode}, is an output
 * node that allows you to retrieve the signal value. Call the
 * {@link Node#toInt()} method on a node to chain a {@code IntNode} to
 * the end:
<pre>
import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.signal.IntNode;

IntNode intNode;

void setup() {
  Patchbay patchbay = new Patchbay();
  intNode = patchbay.osc().address("/input").toInt();
}

void draw() {
  int currentValue = intNode.get();
}
</pre>
 */
public class IntNode extends Node {
  private int[] lastValues = new int[0];
  private int defaultValue;

  public IntNode withDefault(int defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastValues = new int[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    lastValues = Utils.initInts(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = (int) Math.floor(values[i]);
    }
    setDownstream(lastValues);
  }

  @Override
  public void setValue(int[] values) {
    lastValues = values;
    setDownstream(lastValues);
  }

  @Override
  public void setValue(boolean[] values) {
    lastValues = Utils.initInts(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i] ? 1 : 0;
    }
    setDownstream(lastValues);
  }

  public int[] getArray() {
    return lastValues;
  }

  public int get() {
    return (lastValues == null || lastValues.length == 0) ? defaultValue : lastValues[0];
  }
}
