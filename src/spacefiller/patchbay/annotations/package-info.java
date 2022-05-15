/**
 * <p>
 * Annotations are a convenient way to tell Patchbay that a variable or
 * function in your program should be controllable. An annotation always appears
 * directly before the variable or function you want to control. For example,
 * you can use the {@link spacefiller.patchbay.annotations.Osc} annotation to
 * mark a variable as OSC controllable:
<pre>
&#64;Osc
public float x;
</pre>
 * <p>
 * You can tell the annotation by the <code>{@literal @}</code> symbol — all annotations
 * are prefixed with <code>{@literal @}</code>.
 * <p>
 * Note that all variables and functions annotated for Patchbay must also be
 * marked as <code>public</code> as well. This allows Patchbay to externally
 * control that variable or function.
 * <p>
 * Some annotations can also accept parameters. For example, when using the
 * {@link spacefiller.patchbay.annotations.Midi} annotation, you can specify
 * a controller index like this:
<pre>
&#64;Midi(controller = 42)
public float y;
</pre>
 * <p>
 * In addition to annotations for OSC and MIDI, Patchbay also includes a
 * collection of annotations for modifying the incoming signal. For example,
 * to smooth the incoming value:
<pre>
&#64;Midi(controller = 42)
&#64;Smooth(0.2)
public float y;
</pre>
 * <p>
 * There is a one-to-one correspondence between the annotations in this package
 * and the signal nodes defined in {@link spacefiller.patchbay.signal}. The
 * annotations are easier to use, but not as flexible. If you find yourself
 * wanting more control over the incoming signal, you can completely replace
 * annotations with manual signal routing.
 */
package spacefiller.patchbay.annotations;