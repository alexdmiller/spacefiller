package spacefiller.patchbay;

import spacefiller.patchbay.annotations.*;
import spacefiller.patchbay.annotations.Absolute;
import spacefiller.patchbay.annotations.Add;
import spacefiller.patchbay.annotations.Constrain;
import spacefiller.patchbay.annotations.Decay;
import spacefiller.patchbay.annotations.Impulse;
import spacefiller.patchbay.annotations.Invert;
import spacefiller.patchbay.annotations.Max;
import spacefiller.patchbay.annotations.Multiply;
import spacefiller.patchbay.annotations.Print;
import spacefiller.patchbay.annotations.RateOfChange;
import spacefiller.patchbay.annotations.Scale;
import spacefiller.patchbay.annotations.Smooth;
import spacefiller.patchbay.annotations.Sum;
import spacefiller.patchbay.midi.MidiChannelNode;
import spacefiller.patchbay.midi.MidiControlSubscription;
import spacefiller.patchbay.midi.MidiNoteSubscription;
import spacefiller.patchbay.midi.MidiRouter;
import spacefiller.patchbay.osc.OscRouter;
import spacefiller.patchbay.signal.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import processing.core.PApplet;

/**
 * <p>
 * The main Patchbay library class — the starting point for using the library.
 * <p>
 * If you are using Processing, initialize the library by constructing the
 * object like so:
<pre>
Patchbay patchbay = new Patchbay(this);
</pre>
 * <p>
 * This will autoroute annotated variables and functions in your Processing
 * program. See {@link spacefiller.patchbay.annotations} for more information
 * on annotations.
 * <p>
 * This top level object also gives you access to the default MIDI and OSC
 * routers via {@link #midi()} and {@link #osc()}. You can use these references
 * to manually create routes and signal chains for your program. This is an
 * advanced use case, and probably not relevant for many users. See
 * {@link spacefiller.patchbay.signal} for more information.
 */
public class Patchbay {
  private static String DEFAULT_OSC_IP = "127.0.0.1";
  private static int DEFAULT_OSC_PORT = 12000;

  private MidiRouter midiRouter;
  private OscRouter oscRouter;
  private PApplet parent;

  /**
   * <p>
   * Construct a new Patchbay with no autorouted Processing sketch. You can
   * still autoroute an object by calling {@link #autoroute(Object)}.
   * <p>
   * When using the library in this way (decoupled from Processing), it is
   * necessary to call {@link #dispose()} on your own. This will cleanly remove
   * your program from the list of available OSC applications on your computer.
   */
  public Patchbay() {
    this(null);
  }

  /**
   * <p>
   * Construct a new Patchbay and autoroute annotated fields and methods for
   * the passed PApplet. Autorouting will automatically create MIDI or OSC
   * listeners for these annotated fields. See {@link #autoroute(Object)} for
   * details.
   * <p>
   * If using Patchbay with Processing in this way, there is no need to call
   * {@link #dispose()}; it will also be handled automatically.
   *
   * @param parent The parent PApplet object representing the Processing sketch.
   */
  public Patchbay(PApplet parent) {
    this(parent, DEFAULT_OSC_IP, DEFAULT_OSC_PORT);
  }

  /**
   * <p>
   * Construct a new Patchbay and autoroute annotated fields and methods for
   * the passed PApplet. Autorouting will automatically create MIDI or OSC
   * listeners for these annotated fields. See {@link #autoroute(Object)} for
   * details.
   * <p>
   * If using Patchbay with Processing in this way, there is no need to call
   * {@link #dispose()}; it will also be handled automatically.
   *
   * This version of the constructor allows a custom IP and port for OSC
   * integration.
   *
   * @param parent The parent PApplet object representing the Processing sketch.
   * @param oscIp The IP address to listen for OSC messages.
   * @param oscPort The port number to listen for OSC messages.
   */
  public Patchbay(PApplet parent, String oscIp  , int oscPort) {
    this.midiRouter = new MidiRouter();
    this.oscRouter = new OscRouter(oscIp, oscPort);
    this.parent = parent;

    if (parent != null) {
      autoroute(parent);
      parent.registerMethod("dispose", this);
    }
  }

  /**
   * <p>
   * Cleanly stop the library. If you are using the library with Processing,
   * it is not necessary to call this yourself (see {@link #Patchbay(PApplet)}).
   * <p>
   * If you are using this library independent of Processing, then you should
   * call this before your program exits. This will cleanup your program from
   * lists of available OSC applications on your computer.
   */
  public void dispose() {
    oscRouter.stop();
  }

  /**
   * <p>
   * Get the default MidiRouter.
   */
  public MidiRouter midi() {
    return midiRouter;
  }

  /**
   * <p>
   * Get the default OscRouter.
   */
  public OscRouter osc() {
    return oscRouter;
  }

  /**
   * <p>
   * Patchbay consists of routers ({@link OscRouter}, {@link MidiRouter}) that
   * map incoming events to targets within your program. There are two ways
   * to define this mapping: manually, using signal {@link Node}s, or
   * automatically, using annotations like {@link Osc} and {@link Midi}. In most
   * cases, automatic routing (or autorouting) is the simplest way to go.
   * Add these annotations to fields and methods in your program that you wish
   * to hook up to OSC/MIDI, and then call {@code autoroute} on the container
   * object. See {@link spacefiller.patchbay.annotations} for a list of
   * available annotations.
   * <p>
   * If you are using this library with Processing, then your Processing program
   * will be autorouted for you without calling this method (see
   * {@link #Patchbay(PApplet)}).
   * <p>
   * Note that this method can be applied multiple times to different objects.
   *
   * @param object The annotated object to automatically create routes for.
   */
  public void autoroute(Object object) {
    for (Method method : object.getClass().getMethods()) {
      if (method.isAnnotationPresent(Midi.class)) {
        Midi midi = method.getAnnotation(Midi.class);

        String device = midi.device().isEmpty() ? null : midi.device();
        int channel = midi.channel();
        int note = midi.note();
        int control = midi.control();

        Node chain;
        if (control != -1) {
          chain = midi().control(device, channel, control);
        } else if (note != -1) {
          chain = midi().note(device, channel, note);
        } else {
          chain = midi().notes(device, channel);
        }

        if (method.getParameterCount() > 0) {
          chain.setTypeHint(method.getParameterTypes()[0]);
        } else {
          chain.setTypeHint(null);
        }

        chain = buildChain(chain, method.getAnnotations());

        TargetNode target = new MethodTarget(object, method);
        target.setExpectedMin(0);
        target.setExpectedMax(1);
        chain.send(target);
      }

      if (method.isAnnotationPresent(Osc.class)) {
        Osc osc = method.getAnnotation(Osc.class);
        String control = osc.value().isEmpty() ? "/" + method.getName() : osc.value();

        Node chain = osc().address(control);

        if (method.getParameterCount() > 0) {
          chain.setTypeHint(method.getParameterTypes()[0]);
        } else {
          chain.setTypeHint(null);
        }

        chain = buildChain(chain, method.getAnnotations());

        TargetNode target = new MethodTarget(object, method);
        chain.send(target);
      }
    }

    for (Field field : object.getClass().getFields()) {
      if (field.isAnnotationPresent(Midi.class)) {
        Midi midi = field.getAnnotation(Midi.class);

        String device = midi.device().isEmpty() ? null : midi.device();
        int channel = midi.channel();
        int note = midi.note();
        int control = midi.control();

        Node chain;
        if (control != -1) {
          chain = midi().control(device, channel, control);
        } else if (note != -1) {
          chain = midi().note(device, channel, note);
        } else {
          chain = midi().notes(device, channel);
          try {
            if (field.getType() == float[].class) {
              field.set(object, new float[127]);
            } else if (field.getType() == boolean[].class) {
              field.set(object, new boolean[127]);
            } else if (field.getType() == int[].class) {
              field.set(object, new int[127]);
            } else {
              throw new Error("Cannot apply @Midi annotation for a channel to a" +
                  " field of this type (must be float[], boolean[] or int[]).");
            }

          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }

        chain.setTypeHint(field.getType());

        if (field.getType() == float[].class
            || field.getType() == float.class) {
          chain = chain.toFloat();
        }

        if (field.getType() == boolean.class) {
          chain = chain.toBoolean();
        }

        chain = buildChain(chain, field.getAnnotations());

        TargetNode target = new FieldTarget(object, field);
        target.setExpectedMin(0);
        target.setExpectedMax(1);
        chain.send(target);
      }

      if (field.isAnnotationPresent(Osc.class)) {
        if (field.getType().isPrimitive()) {
          Osc osc = field.getAnnotation(Osc.class);

          String control = osc.value().isEmpty() ? "/" + field.getName() : osc.value();

          Node chain = osc().address(control);
          chain.setTypeHint(field.getType());

          chain = buildChain(chain, field.getAnnotations());

          TargetNode target = new FieldTarget(object, field);
          chain.send(target);

        } else {
          throw new UnsupportedOperationException("Cannot add @Osc annotation to non-primitive field.");
        }
      }
    }
  }

  private Node buildChain(Node tail, Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (annotation instanceof Absolute) {
        tail = tail.abs();
      } else if (annotation instanceof Add) {
        Add addAnnotation = (Add) annotation;
        tail = tail.add(addAnnotation.value());
      } else if (annotation instanceof Constrain) {
        Constrain constrainAnnotation = (Constrain) annotation;
        tail = tail.constrain(constrainAnnotation.min(), constrainAnnotation.max());
      } else if (annotation instanceof Decay) {
        Decay decayAnnotation = (Decay) annotation;
        tail = tail.decay(decayAnnotation.value());
      } else if (annotation instanceof Impulse) {
        Impulse impulseAnnotation = (Impulse) annotation;
        tail = tail.impulse(impulseAnnotation.value());
      } else if (annotation instanceof Invert) {
        tail = tail.invert();
      } else if (annotation instanceof Max) {
        tail = tail.max();
      } else if (annotation instanceof Multiply) {
        Multiply multiplyAnnotation = (Multiply) annotation;
        tail = tail.multiply(multiplyAnnotation.value());
      } else if (annotation instanceof Print) {
        Print printAnnotation = (Print) annotation;
        tail = tail.print(printAnnotation.value());
      } else if (annotation instanceof RateOfChange) {
        tail = tail.rate();
      } else if (annotation instanceof Smooth) {
        Smooth smoothAnnotation = (Smooth) annotation;
        tail = tail.smooth(smoothAnnotation.value());
      } else if (annotation instanceof Scale) {
        Scale scaleAnnotation = (Scale) annotation;
        tail = tail.scale(scaleAnnotation.min(), scaleAnnotation.max());
      } else if (annotation instanceof Sum) {
        tail = tail.sum();
      }
    }

    return tail;
  }

  public void printRoutes() {
    System.out.println();
    System.out.println("MIDI ROUTES");
    System.out.println("===========");
    System.out.println();

    System.out.println("CONTROLS");
    System.out.println("--------");
    Map<MidiControlSubscription, Node> controlSubscribers = midiRouter.getControlSubscribers();
    List<MidiControlSubscription> controlSubscriptions =
        controlSubscribers.keySet().stream().collect(Collectors.toList());
    Collections.sort(controlSubscriptions, (o1, o2) -> {
      if (o1.getDevice() != null && o2.getDevice() != null &&
          o1.getDevice().compareTo(o2.getDevice()) != 0) {
        return o1.getDevice().compareTo(o2.getDevice());
      }
      if (o1.getChannel() != o2.getChannel()) {
        return o1.getChannel() - o2.getChannel();
      }
      if (o1.getControl() != o2.getControl()) {
        return o1.getControl() - o2.getControl();
      }
      return 0;
    });
    controlSubscriptions.forEach((subscription) -> {
      Node node = controlSubscribers.get(subscription);
      System.out.print("<");
      System.out.print(subscription.getDevice() != null ? subscription.getDevice() + ", " : "*, ");
      System.out.print(subscription.getChannel() != -1 ? "channel " + subscription.getChannel() + ", " : "*, ");
      System.out.print(subscription.getControl() != -1 ? "control " + subscription.getControl() + " " : "*");
      System.out.print(">");
      printPath(node);
      System.out.println();
    });

    System.out.println("CHANNELS");
    System.out.println("--------");
    Map<MidiNoteSubscription, MidiChannelNode> channelSubscribers =
        midiRouter.getChannelSubscribers();
    List<MidiNoteSubscription> channelSubscriptions =
        channelSubscribers.keySet().stream().collect(Collectors.toList());
    Collections.sort(channelSubscriptions, (o1, o2) -> {
      if (o1.getDevice() != null && o2.getDevice() != null &&
          o1.getDevice().compareTo(o2.getDevice()) != 0) {
        return o1.getDevice().compareTo(o2.getDevice());
      }
      if (o1.getChannel() != o2.getChannel()) {
        return o1.getChannel() - o2.getChannel();
      }
      if (o1.getNote() != o2.getNote()) {
        return o1.getNote() - o2.getNote();
      }
      return 0;
    });

    channelSubscriptions.forEach((subscription) -> {
      Node node = channelSubscribers.get(subscription);
      System.out.print("<");
      System.out.print(subscription.getDevice() != null ? subscription.getDevice() + ", " : "*, ");
      System.out.print(subscription.getChannel() != -1 ? "channel " + subscription.getChannel() + ", " : "*, ");
      System.out.print(subscription.getNote() != -1 ? "note " + subscription.getNote() : "*");
      System.out.print(">");
      printPath(node);
      System.out.println();
    });

    System.out.println("NOTES");
    System.out.println("-----");
    Map<MidiNoteSubscription, Node> noteSubscribers = midiRouter.getNoteSubscribers();
    List<MidiNoteSubscription> noteSubscriptions =
        noteSubscribers.keySet().stream().collect(Collectors.toList());
    Collections.sort(noteSubscriptions, (o1, o2) -> {
      if (o1.getDevice() != null && o2.getDevice() != null &&
          o1.getDevice().compareTo(o2.getDevice()) != 0) {
        return o1.getDevice().compareTo(o2.getDevice());
      }
      if (o1.getChannel() != o2.getChannel()) {
        return o1.getChannel() - o2.getChannel();
      }
      if (o1.getNote() != o2.getNote()) {
        return o1.getNote() - o2.getNote();
      }
      return 0;
    });
    noteSubscriptions.forEach((subscription) -> {
      Node node = noteSubscribers.get(subscription);
      System.out.print("<");
      System.out.print(subscription.getDevice() != null ? subscription.getDevice() + ", " : "*, ");
      System.out.print(subscription.getChannel() != -1 ? "channel " + subscription.getChannel() + ", " : "*, ");
      System.out.print(subscription.getNote() != -1 ? "note " + subscription.getNote() : "*");
      System.out.print(">");
      printPath(node);
      System.out.println();
    });

    System.out.println();
    System.out.println("OSC ROUTES");
    System.out.println("==========");
    System.out.println("TODO");
  }

  private void printPath(Node n) {
    if (n.getClass() != PassThrough.class && n.getClass() != MidiChannelNode.class) {
      System.out.print(" → " + n);
    }
    if (n.getDownstream().size() == 1) {
      printPath(n.getDownstream().get(0));
    }
    if (n.getDownstream().size() > 1) {
      System.out.println("Warning: can't print signal chains with splits.");
    }
  }

  public void syncOn() {
    midiRouter.syncOn();
    oscRouter.syncOn();
  }

  public void syncOff() {
    midiRouter.syncOff();
    oscRouter.syncOff();
  }

  public void update() {
    midiRouter.update();
    oscRouter.update();
  }
}
