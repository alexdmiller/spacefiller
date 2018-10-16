package influencer;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class SceneHost {
  private static SceneHost instance;

  public static SceneHost getInstance() {
    if (instance == null) {
      instance = new SceneHost();
    }

    return instance;
  }

  private List<SceneContainer> containers;

  public SceneHost() {
    containers = new ArrayList<>();
  }

  public void start(Scene scene) {
    SceneContainer container = new SceneContainer(scene);
    containers.add(container);
  }
}
