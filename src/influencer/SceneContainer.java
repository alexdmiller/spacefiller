package influencer;

import codeanticode.syphon.SyphonServer;
import processing.core.PApplet;

public class SceneContainer {
  private Scene scene;
  private SyphonServer server;

  public SceneContainer(Scene scene) {
    this.scene = scene;
    PApplet.runSketch(new String[]{scene.getClass().getSimpleName()}, scene);

    this.server = new SyphonServer(scene, "");

    scene.registerMethod("post", this);
  }

  public void post() {
    server.sendScreen();
  }
}
