package spacefiller.spaceplants;

import java.util.HashMap;
import java.util.Map;

public class Params {
  public static final boolean TEST_MODE = false;

  private static final Map<PName, Object> parameterMap = new HashMap<>();

  public static float f(PName name) {
    if (parameterMap.containsKey(name)) {
      return (float) parameterMap.get(name);
    } else {
      return (float) name.getDefaultValue();
    }
  }

  public static int i(PName name) {
    if (parameterMap.containsKey(name)) {
      return (int) parameterMap.get(name);
    } else {
      return (int) name.getDefaultValue();
    }
  }

  public static boolean b(PName name) {
    if (parameterMap.containsKey(name)) {
      return (boolean) parameterMap.get(name);
    } else {
      return (boolean) name.getDefaultValue();
    }
  }

  public static void set(PName name, int value) {
    parameterMap.put(name, value);
  }

  public static float getStrokeWeight(float brightness) {
    return 2;
  }

  public static float getFluidVelocityScale(float brightness) {
    return (0.1f - 0.035f) * brightness + 0.035f;
  }
}
