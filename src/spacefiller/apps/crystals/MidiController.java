package spacefiller.apps.crystals;

import spacefiller.crystals.engine.Engine;
import spacefiller.patchbay.annotations.Midi;

public class MidiController {
  enum Mode {
    SAVES,
    MAPS,
    SEEDS
  }

  private Mode currentMode = Mode.SAVES;
  private Engine engine;

  public MidiController(Engine engine) {
    this.engine = engine;
  }

  @Midi(channel = 0)
  public void controllerEvent(int note) {
    note = note % 40;

    if (note >= 0 && note < 40) {
      int x = note % 8;
      int y = 5 - note / 8 - 1;

      int index = x + y * 8;
      switch (currentMode) {
        case SAVES:
          engine.setStateIndex(index);
          break;
        case MAPS:
          engine.setMapIndex(index);
          break;
        case SEEDS:
          engine.setSeedIndex(index);
          break;
      }
    }
  }

  @Midi(channel = 0, control = 16)
  public void controlButton1() {
    currentMode = Mode.SAVES;
  }

  @Midi(channel = 1, control = 16)
  public void controlButton2() {
    currentMode = Mode.MAPS;
  }

  @Midi(channel = 2, control = 16)
  public void controlButton3() {
    currentMode = Mode.SEEDS;
  }

}
