package spacefiller.apps.patchbay;

import spacefiller.patchbay.Patchbay;
import spacefiller.patchbay.annotations.Midi;
import spacefiller.patchbay.annotations.Print;

public class MidiFunctionControl {
  public static void main(String[] args) throws InterruptedException {
    MidiFunctionControl c = new MidiFunctionControl();

    while (true) {
      Thread.sleep(10);
      System.out.println(c.on);
    }
  }

  @Midi(channel = 1, note = 0)
  public boolean on;

  public MidiFunctionControl() {
    Patchbay p = new Patchbay();
    p.autoroute(this);
  }

  @Midi(channel = 0)
  public void midiNoteFunction(int note) {
    System.out.println(note);
  }
}
