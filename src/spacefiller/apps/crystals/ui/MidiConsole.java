package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.MidiMapping;
import spacefiller.crystals.engine.MidiMapping.Source;
import processing.core.PApplet;

import java.util.List;
import java.util.Map;

public class MidiConsole extends Component {
  private Engine engine;
  public MidiConsole(PApplet applet, Engine engine) {
    super(applet);
    this.engine = engine;
  }

  @Override
  protected void doDraw() {
    Map<Source, List<Integer>> midiNotes = engine.getMidiNotes();
    for (Source source : midiNotes.keySet()) {
      if (source.type == MidiMapping.Type.MIDI) {
        List<Integer> notes = midiNotes.get(source);
        applet.pushMatrix();
        applet.translate((source.channel - 1) * 100, 0);
        applet.text("channel " + (source.channel  + 1), 0, 0);
        applet.stroke(255);
        applet.translate(0, 20);
        applet.line(0, 0, 90, 0);
        applet.translate(0, 10);
        for (Integer note : notes) {
          applet.text(note, 0, 0);
          applet.translate(0, 15);
        }
        applet.popMatrix();
      }
    }
  }

  @Override
  public float getWidth() {
    return 300;
  }

  @Override
  public float getHeight() {
    return 100;
  }
}
