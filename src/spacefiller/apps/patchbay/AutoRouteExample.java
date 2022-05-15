package spacefiller.apps.patchbay;

import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.annotations.*;

public class AutoRouteExample {
  public static void main(String[] args) throws InterruptedException {
    AutoRouteExample example = new AutoRouteExample();
  }

  @Osc
  @Smooth(0.9f)
  @Scale(min = 0, max = 100)
  public float particleSpeed;

  @Osc
  @Scale(min = 0, max = 20)
  public int integerTest;

  // Using the `Scale` annotation gives patchbay a hint at the expected min and max
  // values.
  @Osc
  @Scale(min = 0, max = 20)
  public float scaleTest;

  @Osc("/custom_address")
  public float customAddress;

  @Midi
  @Print("anyNote")
  public float[] anyNote;

  @Midi(channel = 0)
  @Print("notesInChannel")
  public boolean[] notesInChannel;

  @Midi(channel = 0, note = 0)
  @Print("singleNote")
  public float singleNote;

  @Midi(control = 1)
  @Print("midiControl")
  public float midiControl;

  @Midi(note = 2)
  @Print("singleNoteAllChannels")
  public float singleNoteAllChannels;

  public AutoRouteExample() {
    Patchbay patchbay = new Patchbay();
    patchbay.midi().log();
    patchbay.osc().log();
    patchbay.autoroute(this);

    patchbay.printRoutes();
  }

  @Osc
  public void testTrigger() {
    System.out.println("HELLO");
  }

  @Osc
  public void testMethodFloat(float x) {
    System.out.println("x = " + x);
  }

  @Midi(note = 0)
  public void testMidiMethod() {
    System.out.println("Test midi trigger!");
  }

  @Midi(control = 0)
  public void testMidiControl(float x) {
    System.out.println("Test midi control = " + x);
  }
}
