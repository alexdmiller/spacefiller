package spacefiller.patchbay.signal;

/**
 * Maps the input signal to a range. Only works for normalized float signals.
 */
public class Scale extends TransformNode {
  private float min, max;

  public Scale(float min, float max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public float transform(float value, boolean normalized) {
    if (normalized) {
      return value * (max - min) + min;
    } else {
      throw new IllegalArgumentException("The Scale Node does not support denormalized values.");
    }
  }

  @Override
  public int transform(int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean transform(boolean value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean maintainsNormalizedValues() {
    return false;
  }

  @Override
  public FloatNode toFloat() {
    return super.toFloat().withDefault(min);
  }

  @Override
  public IntNode toInt() {
    return super.toInt().withDefault(Math.round(min));
  }
}
