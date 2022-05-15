package spacefiller.patchbay.signal;

/**
 * Abstract class for all nodes which perform pure, per-channel
 * transformations on input signals.
 */
public abstract class TransformNode extends Node {
  public abstract float transform(float value, boolean normalized);
  public abstract int transform(int value);
  public abstract boolean transform(boolean value);
  public abstract boolean maintainsNormalizedValues();

  @Override
  public final void setValue(float[] values, boolean normalized) {
    for (int i = 0; i < values.length; i++) {
      values[i] = transform(values[i], normalized);
    }

    setDownstream(values, maintainsNormalizedValues());
  }

  @Override
  public final void setValue(int[] values) {
    for (int i = 0; i < values.length; i++) {
      values[i] = transform(values[i]);
    }

    setDownstream(values);
  }

  @Override
  public final void setValue(boolean[] values) {
    for (int i = 0; i < values.length; i++) {
      values[i] = transform(values[i]);
    }

    setDownstream(values);
  }
}
