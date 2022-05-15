package spacefiller.patchbay.signal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * <p>
 * Route a signal to a method of an object.
 * <p>
 * Call the {@link Node#toMethod(Object, String)} method on a node to chain a
 * {@code MethodTarget} node to the end:
<pre>
patchbay.osc().address("/input").toMethod(myObject, "myMethodName");
</pre>
 */
public class MethodTarget extends TargetNode {
  private Object parent;
  private Method method;
  private boolean[] lastBooleanValues;

  public MethodTarget(Object parent, Method method) {
    this.parent = parent;
    this.method = method;
  }

  @Override
  public Type getType() {
    if (method.getParameterCount() > 0) {
      return method.getParameterTypes()[0];
    } else {
      return null;
    }
  }

  @Override
  public String getName() {
    return method.getName();
  }

  @Override
  public Object getValue() {
    return null;
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    try {
      if (this.method.getParameterTypes().length == 0) {
        method.invoke(parent);
      } else if (this.method.getParameterTypes()[0] == float[].class) {
        method.invoke(parent, values);
      } else if (this.method.getParameterTypes()[0] == int.class) {
        method.invoke(parent, (int) values[0]);
      } else {
        method.invoke(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setValue(int[] values) {
    try {
      if (this.method.getParameterTypes()[0] == int[].class) {
        method.invoke(parent, values);
      } else {
        method.invoke(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void setValue(boolean[] values) {
    try {
      if (method.getParameterCount() == 0) {
        if (values[0]) {
          // If a method has no parameters, interpret true/false to mean "trigger the method or don't"
          method.invoke(parent);
        }
      } else if (this.method.getParameterTypes()[0] == boolean[].class) {
        method.invoke(parent, values);
      } else if (this.method.getParameterTypes()[0] == int.class) {
        // Special case for a method with the following signature:
        //
        //   @Midi(channel = 0)
        //   public void myFunction(int note)
        //
        // We want to invoke this method with the index of the note that
        // changed.
        int changedIndex = findChangedIndex(lastBooleanValues, values);
        if (changedIndex > -1) {
          method.invoke(parent, changedIndex);
        }
      } else {
        method.invoke(parent, values[0]);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    lastBooleanValues = values;
  }

  private int findChangedIndex(boolean[] prev, boolean[] next) {
    for (int i = 0; i < next.length; i++) {
      if (next[i]) {
        if (prev != null && i < prev.length) {
          if (!prev[i]) {
            // If there is a previous array and this item was false
            // (and it is now true), then return that index
            return i;
          }
        } else {
          // If there is no previous array, return the index of the first
          // true value in `next`
          return i;
        }
      }
    }
    return -1;
  }

  @Override
  public String toString() {
    return parent.getClass().getSimpleName() + "." + method.getName();
  }

}
