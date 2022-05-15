package spacefiller.patchbay.signal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.function.Consumer;

/**
 * <p>
 * The base class for signal nodes.
 * <p>
 * In regular use of the library, {@code Node} objects are not constructed
 * manually. Instead, input nodes are created by using the OSC or MIDI routers:
<pre>
// A single-channel signal from OSC address /myAddress:
Node oscInputNode = patchbay.osc().address("/myAddress");

// All notes from MIDI channel 0:
Node midiInputNode = patchbay.midi().notes(0);
</pre>
 * <p>
 * After an input node, modifier nodes can be chained onto it:
<pre>
Node smooth = oscInputNode.invert().smooth(0.2);
</pre>
 * <p>
 * Finally, an output node can chained at the end. Output nodes consist of:
<ul class="list">
 <li>{@link FloatNode}: output signal as a float</li>
 <li>{@link IntNode}: output signal as an int</li>
 <li>{@link BooleanNode}: output signal as a boolean</li>
 <li>{@link MethodTarget}: route output to a method</li>
 <li>{@link FieldTarget}: route output to a field</li>
</ul>
 * <p>As an example:
<pre>
smooth.toField(myObject, "myFieldName");
</pre>
 */
public abstract class Node extends Observable {
  protected List<Node> downstream;
  private int channelHint;

  // the expected bounds of values to this node,
  // not necessarily respected by the client
  private float expectedMin = 0;
  private float expectedMax = 1;

  private Type typeHint = float.class;

  public Node() {
    downstream = new ArrayList<>();
    setChanged();
  }

  public abstract void setValue(float[] values, boolean normalized);
  public abstract void setValue(int[] values);
  public abstract void setValue(boolean[] values);

  protected int getChannelHint() {
    return channelHint;
  }

  protected void setChannelHint(int channelHint) {
    this.channelHint = channelHint;
  }

  public final void setValue(float value) {
    setValue(value, false);
  }

  public final void setValue(float[] value) {
    setValue(value, false);
  }

  public final void setValue(float value, boolean normalized) {
    setValue(new float[] { value }, normalized);
  }

  public final void setValue(int value) {
    setValue(new int[] { value });
  }

  public final void setValue(boolean value) {
    setValue(new boolean[] { value });
  }

  protected final void setDownstream(float value, boolean normalized) {
    setDownstream(new float[] { value }, normalized);
  }

  protected final void setDownstream(int value) {
    setDownstream(new int[] { value });
  }

  protected final void setDownstream(boolean value) {
    setDownstream(new boolean[] { value });
  }

  protected final void setDownstream(float values[], boolean normalized) {
    for (Node d : downstream) {
      if (d != null) {
        d.setValue(Arrays.copyOf(values, values.length), normalized);
      }
    }
  }

  protected final void setDownstream(int values[]) {
    for (Node d : downstream) {
      if (d != null) {
        d.setValue(Arrays.copyOf(values, values.length));
      }
    }
  }

  protected final void setDownstream(boolean values[]) {
    for (Node d : downstream) {
      if (d != null) {
        d.setValue(Arrays.copyOf(values, values.length));
      }
    }
  }

  /**
   * Given the following graph:
   *
   * <pre>
   *    +---+    +---+
   *    | a +---~| b |
   *    +---+    +---+
   * </pre>
   *
   * a.insert(i) results in the following graph:
   *
   * <pre>
   *    +---+    +---+    +---+
   *    | a +---~| i +---~| b +
   *    +---+    +---+    +---+
   * </pre>
   *
   * @param receiver
   * @return
   */
  public Node insert(Node receiver) {
    receiver.setChannelHint(channelHint);
    receiver.setExpectedMax(expectedMax);
    receiver.setExpectedMin(expectedMin);
    receiver.downstream = this.downstream;
    this.downstream = new ArrayList<>();
    this.downstream.add(receiver);
    return receiver;
  }

  /**
   * a.send(b) returns a node graph with the following flow:
   *
   * <pre>
   *    +---+    +---+
   *    | a +---~| b |
   *    +---+    +---+
   * </pre>
   *
   * @param receiver
   * @return
   */
  public Node send(Node receiver) {
    receiver.setChannelHint(channelHint);
    this.downstream.add(receiver);
    return receiver;
  }

  public Add add(float factor) {
    return (Add) send(new Add(factor));
  }

  public Multiply multiply(float factor) {
    return (Multiply) send(new Multiply(factor));
  }

  public Invert invert() {
    return (Invert) send(new Invert());
  }

  public Smooth smooth(float easeAmount) {
    return (Smooth) send(new Smooth(easeAmount));
  }

  public Decay decay(float decayAmount) {
    return (Decay) send(new Decay(decayAmount));
  }

  public Impulse impulse(float threshold) { return (Impulse) send(new Impulse(threshold)); }

  public Scale scale(float min, float max) {
    return (Scale) send(new Scale(min, max));
  }

  public Gate gate(float threshold) {
    return (Gate) send(new Gate(threshold));
  }

  public RateOfChange rate() {
    return (RateOfChange) send(new RateOfChange());
  }

  public Absolute abs() {
    return (Absolute) send(new Absolute());
  }

  public Constrain constrain(float min, float max) {
    return (Constrain) send(new Constrain(min, max));
  }

  public Print print() {
    return (Print) send(new Print());
  }

  public Print print(String tag) {
    return (Print) send(new Print(tag));
  }

  public Sum sum() {
    return (Sum) send(new Sum());
  }

  public Max max() {
    return (Max) send(new Max());
  }

  public FieldTarget toField(Object object, String fieldName) {
    try {
      return (FieldTarget) send(new FieldTarget(object, object.getClass().getField(fieldName)));
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    return null;
  }

  public MethodTarget toMethod(Object object, String methodName) {
    try {
      return (MethodTarget) send(new MethodTarget(object, object.getClass().getMethod(methodName, int.class)));
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    return null;
  }

  public FunctionTarget toFunction(Consumer<float[]> fn) {
    return (FunctionTarget) send(new FunctionTarget(fn));
  }

  public Node channel(int channel) {
    return send(new PassThrough().setChannel(channel));
  }

  public FloatNode toFloat() {
    FloatNode dr = new FloatNode();
    send(dr);
    return dr;
  }

  public IntNode toInt() {
    IntNode dr = new IntNode();
    send(dr);
    return dr;
  }

  public BooleanNode toBoolean() {
    BooleanNode dr = new BooleanNode();
    send(dr);
    return dr;
  }

  public float getExpectedMin() {
    return expectedMin;
  }

  public void setExpectedMin(float expectedMin) {
    this.expectedMin = expectedMin;
    setChanged();
    notifyObservers();
  }

  public float getExpectedMax() {
    return expectedMax;
  }

  public void setExpectedMax(float expectedMax) {
    this.expectedMax = expectedMax;
    setChanged();
    notifyObservers();
  }

  public Type getTypeHint() {
    return typeHint;
  }

  public void setTypeHint(Type typeHint) {
    this.typeHint = typeHint;
    setChanged();
    notifyObservers();
  }

  public List<Node> getDownstream() {
    return downstream;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
