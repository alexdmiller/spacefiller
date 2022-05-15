package spacefiller.patchbay.signal;

/**
 * <p>
 * Take the absolute value of the signal. Negative values will become positive.
 * <p>
 * Call the {@link Node#abs()} method on a node to chain an {@code Absolute}
 * node to the end:
<pre>
Absolute node = patchbay.osc().address("/input").abs();
</pre>
 * <p>
 * This node does not work for boolean signals.
 */
public class Absolute extends TransformNode {
  @Override
  public float transform(float value, boolean normalized) {
    return Math.abs(value);
  }

  @Override
  public int transform(int value) {
    return Math.abs(value);
  }

  @Override
  public boolean transform(boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean maintainsNormalizedValues() {
    return true;
  }
}
