package spacefiller.patchbay.osc;

import java.lang.reflect.Type;

public enum OscType {
  FLOAT("f"), FLOAT_ARRAY("f["), INTEGER("i"), BOOLEAN("T"), NULL("N");

  private String tag;

  OscType(String tag) {
    this.tag = tag;
  }

  @Override
  public String toString() {
    return tag;
  }

  public static OscType fromType(Type type) {
    if (type == null) {
      return NULL;
    } if (type.getTypeName().equals("int")) {
      return INTEGER;
    } else if (type.getTypeName().equals("float")) {
      return FLOAT;
    } else if (type.getTypeName().equals("boolean")) {
      return BOOLEAN;
    } else if (type.getTypeName().equals("float[]")) {
      return FLOAT_ARRAY;
    }

    throw new Error(type.getTypeName() + " is not a supported osc type.");
  }
}
