package spacefiller.patchbay.signal;

/**
 * <p>
 * Constrains or clamps the signal between a minimum and maximum value.
 * <p>
 * This node does not work with integer or boolean signals.
 */
public class Constrain extends TransformNode {
  private float min, max;

  public Constrain(float min, float max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public float transform(float value, boolean normalized) {
    return Math.max(min, Math.min(max, value));
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
