package spacefiller.patchbay.signal;

import java.lang.reflect.Type;

/**
 * Abstract class for a node that outputs its value to a target (i.e. a field
 * or method, but potentially future targets as well like OSC or MIDI).
 */
public abstract class TargetNode extends Node {
  public abstract Type getType();
  public abstract String getName();
  public abstract Object getValue();
}
