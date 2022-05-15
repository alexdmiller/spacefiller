package spacefiller.crystals.engine;

import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

public interface Animator {
  void setup();
  void preview(PGraphics graphics);
  void noteOn(int channel, int note);
  void noteOff(int channel, int note);

  abstract class SimpleAnimator implements Animator {
    public abstract void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale);

    public void preview(PGraphics graphics) {
      List<Integer> notes = new ArrayList<>();
      notes.add(1);
      notes.add(5);
      draw(graphics, 60, notes, 1);
    }

    public void setup() { }
    public void noteOn(int channel, int note) { }
    public void noteOff(int channel, int note) { }
  }

  abstract class DoubleAnimator implements Animator {
    public abstract void drawMap(PGraphics map, int frameCount, List<Integer> notes);
    public abstract void drawSeed(PGraphics seed, int frameCount, List<Integer> notes);

    public void preview(PGraphics graphics) {
      List<Integer> notes = new ArrayList<>();
      notes.add(1);
      notes.add(5);
      drawMap(graphics, 60, notes);
      notes.clear();
    }

    public void setup() { }
    public void noteOn(int channel, int note) { }
    public void noteOff(int channel, int note) { }
  }
}
