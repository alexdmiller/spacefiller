package influencer;

import de.looksgood.ani.Ani;
import spacefiller.remote.signal.FloatNode;
import spacefiller.remote.signal.IntNode;

public class CubeGrid extends InfluencerScene {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new CubeGrid());
  }

  private IntNode rowsIn = mainSlider.scale(1, 11).toInt();
  private IntNode colsIn = mainSlider.scale(1, 11).toInt();
  private FloatNode camX = mainSlider.scale(0, 500).smooth(0.1f).toFloat();
  private FloatNode camY = control.controller(14).scale(0, 1000).smooth(0.1f).toFloat();
  private FloatNode camZ = control.controller(15).scale(0, 1000).smooth(0.1f).toFloat();

  private float cellSize = 50;

  @Override
  public void setup() {
    Ani.init(this);
  }

  @Override
  public void mousePressed() {
    // Ani.to(this, 1, "interpolationAmount", 1);
  }

  @Override
  public void draw() {
    background(0);
    ortho();

    int rows = rowsIn.get();
    int cols = colsIn.get();
    float gridWidth = cols * cellSize;
    float gridHeight = rows * cellSize;

    camera(-camX.get(), -camY.get(), camZ.get(), 0, 0, 0, 0, 1, 0);
    scale(2f);

    translate(-gridWidth / 2f, -gridHeight / 2f);

    noFill();
    stroke(100);
    strokeWeight(2);
    pushMatrix();

    float kick = kickDecay.get();
    float snare = snareDecay.get();

    translate(0, 0, + 50 + kick * 30);

    int strokeColor;
    if (snare > 0) {
      strokeColor = lerpColor(color(255), color(0, 255, 255), snare);
    } else if (kick > 0) {
      strokeColor = lerpColor(color(255), color(255, 0, 255), kick);
    } else {
      strokeColor = color(255);
    }

    stroke(strokeColor);
    for (int x = 0; x <= cols; x++) {
      line(x * cellSize, 0, x * cellSize, rows * cellSize);
    }

    for (int y = 0; y <= rows; y++) {
      line(0, y * cellSize, cols * cellSize, y * cellSize);
    }

    popMatrix();

    stroke(255);
    strokeWeight(2);
    fill(0);

    translate(0, 0, -100 - kick * 30);

    stroke(strokeColor);

    float[] notes = decayedNotes.getArray();
    for (int i = 0; i < notes.length; i++) {
      pushMatrix();

      int c = i % (cols*rows);
      int x = c % cols;
      int y = c / cols;

      fill(255 * notes[i]);
      float height = notes[i] * notes[i] * cellSize * 2;
      translate(x * cellSize + cellSize/2, y * cellSize + cellSize/2, height/2);
      box(cellSize, cellSize, height);
      popMatrix();
    }
  }
}
