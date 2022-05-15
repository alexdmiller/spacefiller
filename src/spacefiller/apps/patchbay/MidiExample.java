package spacefiller.apps.patchbay;

import spacefiller.patchbay.midi.MidiRouter;

public class MidiExample {
  public static void main(String[] args) {
    MidiRouter midiRouter = new MidiRouter();
    midiRouter.log();
    midiRouter.notes(0).print();
    midiRouter.control(1).print();

    midiRouter.notes(1).print();

    while (true) {
      continue;
    }
  }
}
