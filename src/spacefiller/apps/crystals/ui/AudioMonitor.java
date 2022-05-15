package spacefiller.apps.crystals.ui;

import crystal.audio.Audio;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.sound.BeatDetector;

public class AudioMonitor extends Component {
  private static final int CHART_HORIZONTAL_SCALE = 4;
  private static final int CHART_VERTICAL_SCALE = 40;

  private Audio audio;
  private int width;

  public AudioMonitor(PApplet applet, Audio audio) {
    super(applet);
    this.audio = audio;

    if (audio.channels() > 0) {
      this.width = audio.getBeatDetector()[0].getEnergyBuffer().length * CHART_HORIZONTAL_SCALE;
    } else {
      this.width = 100;
    }
  }

  @Override
  protected void doDraw() {
    if (audio.channels() == 0) {
      applet.text("Audio device not found.", 0, 0);
    } else {
      for (int i = 0; i < audio.channels(); i++) {
        BeatDetector detector = audio.getBeatDetector()[i];

        double[] energyBuffer = detector.getEnergyBuffer();
        int cursor = detector.getEnergyCursor();
        double last = energyBuffer[cursor];

        applet.pushMatrix();
        applet.translate(0, i * (CHART_VERTICAL_SCALE + 5));

        applet.textAlign(PConstants.LEFT, PConstants.BOTTOM);
        applet.fill(255);
        applet.text(i, 0, CHART_VERTICAL_SCALE);

        applet.translate(15, 0);
        applet.stroke(255);
        for (int j = 1; j < energyBuffer.length; j++) {
          int index = (j + cursor) % energyBuffer.length;
          applet.line((j - 1) * 4, CHART_VERTICAL_SCALE - (float) last * 1, j * 4, CHART_VERTICAL_SCALE - (float) energyBuffer[index] * 1);
          last = energyBuffer[index];
        }

        boolean[] beatBuffer = detector.getBeatBuffer();
        for (int j = 1; j < beatBuffer.length; j++) {
          int index = (j + cursor) % energyBuffer.length;
          boolean beat = beatBuffer[index];
          if (beat) {
            applet.stroke(255, 255, 0);
            applet.line(j * 4, 0, j * 4, CHART_VERTICAL_SCALE);
          }
        }

        applet.translate(energyBuffer.length * CHART_HORIZONTAL_SCALE + 5, 0);

        applet.stroke(255);
        applet.fill(audio.isBeatDetected(i) ? 255 : 0);
        applet.rect(0, 0, 10, 10);

        applet.translate(15, 0);

        applet.rectMode(PConstants.CORNERS);
        applet.stroke(255);
        applet.noFill();
        applet.rect(0, 0, 10, CHART_VERTICAL_SCALE);

        applet.fill(255);
        applet.noStroke();
        applet.rect(0, (1 - audio.amplitude(i)) * CHART_VERTICAL_SCALE, 10, CHART_VERTICAL_SCALE);

        applet.rectMode(PConstants.CORNER);

        applet.popMatrix();
      }
    }
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    return audio.channels() * (CHART_VERTICAL_SCALE + 5);
  }
}
