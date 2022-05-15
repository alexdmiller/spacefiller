package spacefiller.patchbay.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 * Mark a variable or function as a target for incoming MIDI data.
 * <p>
 * This annotation can be used in a few different ways. Here are some examples
 * targeting a variable with a single MIDI note:
<pre>
// When note 5 is pressed on channel 0 of "My Device", noteIsPressed will be set to true.
&#64;Midi(device="My Device", channel=0, note=5)
boolean noteIsPressed;
</pre>
<pre>
// When note 5 is pressed on channel 0 of any device, noteIsPressed will be set to true.
&#64;Midi(channel=0, note=5)
boolean noteIsPressed;
</pre>
<pre>
// When note 5 is pressed on any channel on any device, noteIsPressed will be set to true.
&#64;Midi(note=5)
boolean noteIsPressed;
</pre>
<pre>
// When note 5 is pressed on any channel on any device, noteIsPressed will be set to true.
&#64;Midi(note=5)
boolean noteIsPressed;
</pre>
 * <p>
 * The annotation also allows you to receive an entire channel of notes:
<pre>
// When any note in channel 0 is pressed on "My Device", the notes[] array is updated.
&#64;Midi(device="My Device", channel=0)
boolean[] notes;
</pre>
<pre>
// When any note in channel 0 is pressed on any device, the notes[] array is updated.
&#64;Midi(channel=0)
boolean[] notes;
</pre>
 * <p>
 * TODO: method targets
<pre>
// When any note in channel 0 is pressed on any device, the notes[] array is updated.
&#64;(channel=0)
boolean[] notes;
</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Midi {
  String device() default "";
  int channel() default -1;
  int note() default -1;
  int control() default -1;
}