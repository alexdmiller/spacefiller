package spacefiller.patchbay.signal;

/**
 * Internal utility methods.
 */
public class Utils {
  protected static float[] initFloats(float[] current, int length) {
    if (current == null || current.length != length) {
      return new float[length];
    } else {
      return current;
    }
  }

  protected static int[] initInts(int[] current, int length) {
    if (current == null || current.length != length) {
      return new int[length];
    } else {
      return current;
    }
  }

  protected static boolean[] initBooleans(boolean[] current, int length) {
    if (current == null || current.length != length) {
      return new boolean[length];
    } else {
      return current;
    }
  }
}
