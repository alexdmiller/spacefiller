package spacefiller.patchbay.signal;

import java.util.Arrays;

/**
 * <p>
 * Prints the signal value and passes the signal value through without
 * transforming it. An optional label can be printed along with the value.
 */
public class Print extends Node {
  private String label;

  public Print() {
    this.label = null;
  }

  public Print(String label) {
    this.label = label;
  }

  @Override
  public void setValue(float[] values, boolean normalized) {
    String labelStr = label != null ? " {" + label + "}" : "";
    System.out.println("float = " + Arrays.toString(values) + labelStr);
    setDownstream(values, normalized);
  }

  @Override
  public void setValue(int[] values) {
    String labelStr = label != null ? " {" + label + "}" : "";
    System.out.println("int = " + Arrays.toString(values) + labelStr);
    setDownstream(values);
  }

  @Override
  public void setValue(boolean[] values) {
    String labelStr = label != null ? " {" + label + "}" : "";
    System.out.println("boolean = " + Arrays.toString(values) + labelStr);
    setDownstream(values);
  }
}
