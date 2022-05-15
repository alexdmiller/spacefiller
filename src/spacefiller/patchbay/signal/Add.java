package spacefiller.patchbay.signal;

/**
 * <p>
 * Add a constant value to the signal. The output signal of this node is not
 * normalized.
 * <p>
 * This node does not work for boolean signals.
 */
public class Add extends TransformNode {
  private float b;

  public Add(float b) {
    this.b = b;
  }

  @Override
  public float transform(float value, boolean normalized) {
    return value + b;
  }

  @Override
  public int transform(int value) {
    return Math.round(value + b);
  }

  @Override
  public boolean transform(boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean maintainsNormalizedValues() {
    return false;
  }
}
