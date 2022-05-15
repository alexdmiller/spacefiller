package spacefiller.patchbay.signal;

/**
 * Flip a normalized signal or boolean value. For example, {@code 0} becomes
 * {@code 1} and {@code 1} becomes {@code 0}.
 */
public class Invert extends TransformNode {
  @Override
  public float transform(float value, boolean normalized) {
    if (normalized) {
      return 1 - value;
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public int transform(int value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean transform(boolean value) {
    return !value;
  }

  @Override
  public boolean maintainsNormalizedValues() {
    return true;
  }
}
