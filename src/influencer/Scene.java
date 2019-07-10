package influencer;

import codeanticode.syphon.SyphonServer;
import processing.core.PApplet;
import processing.opengl.PJOGL;
import spacefiller.remote.MidiRemoteControl;

public class Scene extends PApplet {
  public void settings() {
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  protected final MidiRemoteControl midi(String name) {
    return new MidiRemoteControl(name);
  }
}
