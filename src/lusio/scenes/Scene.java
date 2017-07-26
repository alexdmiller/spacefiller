package lusio.scenes;

import graph.Graph;
import lusio.Lightcube;
import lusio.generators.SceneGenerator;
import processing.core.PGraphics;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scene {
  private List<SceneGenerator> generators;

  public Scene() {
    generators = new ArrayList<>();
  }

  public void setup(Map<String, Graph> graphs) { }

  public boolean transitionOut() {
    return true;
  }

  public void teardown() {
    generators.clear();
  }

  public void addGenerator(SceneGenerator generator) {
    generators.add(generator);
  }

  public void draw(Lightcube cube, PGraphics graphics) {
    for (SceneGenerator generator : generators) {
      graphics.pushMatrix();
      graphics.translate(generator.getX(), generator.getY());
      generator.draw(graphics);
      graphics.popMatrix();
    }
  }
}
