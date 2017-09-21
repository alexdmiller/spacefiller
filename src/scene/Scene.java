package scene;

import graph.Graph;
import lightcube.Lightcube;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scene {
  private List<SceneComponent> components;

  public Scene() {
    components = new ArrayList<>();
  }

  public void setup() { }

  public boolean transitionOut() {
    return true;
  }

  public void teardown() {
    components.clear();
  }

  public void addComponent(SceneComponent component) {
    components.add(component);
  }

  public void draw(PGraphics graphics) {
    for (SceneComponent component : components) {
      graphics.pushMatrix();
      graphics.translate(component.getX(), component.getY());
      component.draw(graphics);
      graphics.popMatrix();
    }
  }
}
