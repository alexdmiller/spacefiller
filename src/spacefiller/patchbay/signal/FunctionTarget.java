package spacefiller.patchbay.signal;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class FunctionTarget extends TargetNode {
  Consumer<float[]> fn;
  private boolean[] lastBooleanValues;

  public FunctionTarget(Consumer<float[]> fn) {
    this.fn = fn;
  }

  @Override
  public Type getType() {
    return null;
  }

  @Override
  public String getName() {
    return "Anonymous function";
  }

  @Override
  public Object getValue() {
    return null;
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    this.fn.accept(values);
  }

  @Override
  public void setValue(int[] values) {
    throw new UnsupportedOperationException("Not implemented yet: Can't call setValue with int array on Function target.");
  }

  @Override
  public void setValue(boolean[] values) {
    throw new UnsupportedOperationException("Not implemented yet: Can't call setValue with boolean array on Function target.");
  }

  @Override
  public String toString() {
    return getName();
  }
}
