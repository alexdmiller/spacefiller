package crystal.audio;

import processing.core.PApplet;
import processing.sound.Amplitude;
import processing.sound.AudioIn;
import processing.sound.BeatDetector;
import processing.sound.Sound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Audio {
  private BeatDetector beatDetector[];
  private Amplitude amplitude[];

  private AudioIn input[];
  private float lastVolume[];
  private boolean beatDetected[];
  private int channels;
  private List<BeatListener> listeners;

  public Audio(PApplet parent, int inputDevice, int channels) {
    this.channels = channels;
    this.listeners = new ArrayList<>();

    int deviceIndex = -1;
    String[] devices = Sound.list();
    for (int i = 0; i < devices.length; i++) {
      String name = devices[i];
      if (name.equals("Crystal Device")) {
        deviceIndex = i;
      }
    }
    if (deviceIndex < 0) {
      System.out.println("Audio input could not be initialized.");
      System.out.println("Expected audio device not found.");
      this.channels = 0;
      return;
    }

    Sound s = new Sound(parent);
    s.inputDevice(deviceIndex);

    beatDetector = new BeatDetector[channels];
    amplitude = new Amplitude[channels];
    input = new AudioIn[channels];
    lastVolume = new float[channels];
    beatDetected = new boolean[channels];

    for (int i = 0; i < channels; i++) {
      input[i] = new AudioIn(parent, i);
      input[i].start(1, 0, 0 );

      beatDetector[i] = new BeatDetector(parent);
      beatDetector[i].sensitivity( 1);

      // Patch the input to the volume analyzer
      beatDetector[i].input(input[i]);

      amplitude[i] = new Amplitude(parent);
      amplitude[i].input(input[i]);
    }

  }

  public void update() {
    for (int i = 0; i < channels; i++) {
      beatDetected[i] = beatDetector[i].analyze();
      if (beatDetected[i]) {
        for (BeatListener listener : listeners) {
          listener.onBeat(i);
        }
      }
    }
  }

  public boolean isBeatDetected(int channel) {
    return beatDetected[channel];
  }

  public float amplitude(int channel) {
    return amplitude[channel].analyze();
  }

  public BeatDetector[] getBeatDetector() {
    return beatDetector;
  }

  public int channels() {
    return channels;
  }

  public void listen(BeatListener listener) {
    listeners.add(listener);
  }
}