package spacefiller.patchbay.signal;

/**
 * Multiply the signal by a scalar.
 */
public class Multiply extends TransformNode {
  private float factor;

  public Multiply(float factor) {
    this.factor = factor;
  }

  @Override
  public float transform(float value, boolean normalized) {
    return value * factor;
  }

  @Override
  public int transform(int value) {
    return Math.round(value * factor);
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
