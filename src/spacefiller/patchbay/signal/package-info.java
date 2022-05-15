/**
 * <p>
 * Modify and route incoming OSC and MIDI data.
 * <p>
 * When a MIDI or OSC event occurs, it is processed by a chain of signal nodes.
 * This is the underlying fundamental architecture of Patchbay.
 * It is easier to use {@link spacefiller.patchbay.annotations} rather than
 * create signal chains manually, but Patchbay annotations are not as flexible
 * as using the signal interface directly.
 * <p>
 * A signal chain is represented as a linked-list of nodes, each of which transforms
 * the input in some way before passing a value to the output.
 * <p>
 * The signal can be a single channel (like a single knob on an MIDI
 * controller), or multi-channel (like an entire array of keys on a MIDI
 * keyboard). This versatility allows you to create a signal chain that
 * operates on every key in a keyboard simultaneously.
 * <p>
 * To illustrate how signal nodes work, let us compare using annotations versus
 * signal chains directly. First, here's a Processing sketch with an annotated
 * variable.
 *
<pre>
import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.annotations.Osc;
import spacefiller.patchbay.annotations.Smooth;
import spacefiller.patchbay.annotations.Scale;
import spacefiller.patchbay.annotations.Print;

&#64;Osc
&#64;Smooth(0.2)
&#64;Scale(min = 0, max = 100)
&#64;Print("my variable")
public float myVariable;

void setup() {
  size(200, 200);
  Patchbay patchbay = new Patchbay(this);
}
</pre>
 * <p>
 * And here's a version rewritten without annotations:
<pre>
import spacefiller.patchbay.Patchbay;

public float myVariable;

void setup() {
  size(200, 200);
  Patchbay patchbay = new Patchbay(this);

  patchbay.osc()
    .address("/myVariable")
    .smooth(0.2)
    .scale(0, 100)
    .print("my variable")
    .toField(this, "myVariable");
}
</pre>
 * <p>
 * This annotationless version has a couple of key differences:
 * <ul class="list">
 *   <li>While annotations are placed directly before the variable they refer
 *   to, manually creating a signal chain is accomplished by calling methods
 *   on the {@link spacefiller.patchbay.Patchbay} object in <code>setup</code>.
 *   This decouples the variable declaration from the Patchbay configuration,
 *   which can be annoying.</li>
 *   <li>Annotations create the routing between OSC address and variable
 *   implicitly. But if we define the signal chain manually, we must explicitly
 *   create an OSC endpoint <code>/myVariable</code> and explicitly route it to
 *   the variable (aka a "field") with {@link spacefiller.patchbay.signal.Node#toField(java.lang.Object, java.lang.String)} .</li>
 * </ul>
 * <p>
 * These differences are subtle, and one way is not better than the other.
 * Patchbay offers these two different interfaces so you can use the style
 * you prefer.
 */
package spacefiller.patchbay.signal;