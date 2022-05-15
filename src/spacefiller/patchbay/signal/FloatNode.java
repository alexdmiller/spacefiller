package spacefiller.patchbay.signal;

/**
 * <p>
 * Convert any signal type to a float signal output. {@code true} boolean
 * values will be converted to {@code 1}, and {@code false} boolean values will
 * be converted to {@code 0}.
 * <p>
 * This node, along with {@link IntNode} and {@link BooleanNode}, is an output
 * node that allows you to retrieve the signal value. Call the
 * {@link Node#toFloat()} method on a node to chain a {@code FloatNode} to
 * the end:
<pre>
import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.signal.FloatNode;

FloatNode floatNode;

void setup() {
  Patchbay patchbay = new Patchbay();
  floatNode = patchbay.osc().address("/input").toFloat();
}

void draw() {
  float currentValue = floatNode.get();
}
</pre>
 */
public class FloatNode extends Node {
  private float[] lastValues = new float[0];
  private float defaultValue;

  public FloatNode withDefault(float defaultValue) {
    this.defaultValue = defaultValue;
    return this;
  }

  @Override
  protected void setChannelHint(int channelHint) {
    super.setChannelHint(channelHint);
    lastValues = new float[channelHint];
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    lastValues = values;
    setDownstream(values, normalized);
  }

  @Override
  public void setValue(int[] values) {
    lastValues = Utils.initFloats(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i];
    }
    setDownstream(lastValues, false);
  }

  @Override
  public void setValue(boolean[] values) {
    lastValues = Utils.initFloats(lastValues, values.length);

    for (int i = 0; i < values.length; i++) {
      lastValues[i] = values[i] ? 1f : 0f;
    }
    setDownstream(lastValues, true);
  }

  public float[] getArray() {
    return lastValues;
  }

  public float get() {
    return (lastValues == null || lastValues.length == 0) ? defaultValue : lastValues[0];
  }
}
