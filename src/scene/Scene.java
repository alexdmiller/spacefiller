package scene;

import lightcube.Lightcube;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scene {
  private List<SceneComponent> components;
  protected int width, height;

  // A flag that tracks whether or not setup() has been called yet
  private boolean isSetup;

  // Whether setup() should be called again if isSetup is already true
  private boolean alwaysReset;

  public Scene() {
    components = new ArrayList<>();
  }

  public void setup() {
    isSetup = true;
  }

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

  public void setDimensions(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setAlwaysReset(boolean alwaysReset) {
    this.alwaysReset = alwaysReset;
  }

  public boolean alwaysReset() {
    return alwaysReset;
  }

  public boolean isSetup() {
    return isSetup;
  }
}
