package spacefiller.patchbay.signal;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * <p>
 * Route a signal to the field of an object.
 * <p>
 * Call the {@link Node#toField(Object, String)} method on a node to chain a
 * {@code FieldTarget} node to the end:
<pre>
patchbay.osc().address("/input").toField(myObject, "myFieldName");
</pre>
 */
public class FieldTarget extends TargetNode {
  private Object parent;
  private Field field;

  public FieldTarget(Object parent, Field field) {
    this.parent = parent;
    this.field = field;
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    // TODO: handle any field target type

    try {
      if (this.field.getType() == float[].class) {
        field.set(parent, values);
      } else if (this.field.getType() == float.class) {
        field.set(parent, values[0]);
      } else if (this.field.getType().equals(Integer.TYPE)){
        field.set(parent, (int) Math.round(values[0]));
        field.set(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setValue(int[] values) {
    // TODO: handle any field target type

    try {
      if (this.field.getType() == int[].class) {
        field.set(parent, values);
      } else {
        field.set(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setValue(boolean[] values) {
    try {
      if (this.field.getType() == boolean[].class) {
        field.set(parent, values);
      } else if (this.field.getType() == float.class) {
        field.set(parent, values[0] ? 1f : 0f);
      } else {
        field.set(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Type getType() {
    return field.getType();
  }

  @Override
  public String getName() {
    return field.getName();
  }

  public Object getValue() {
    try {
      return field.get(parent);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    return parent.getClass().getSimpleName() + "." + field.getName();
  }
}
