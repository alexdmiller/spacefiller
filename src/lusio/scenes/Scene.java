package lusio.scenes;

import graph.Graph;
import lusio.generators.SceneGenerator;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scene {
  public List<SceneGenerator> generators;

  public Scene() {
    generators = new ArrayList<>();
  }

  public void setup(Map<String, Graph> graphs) { }

  public void teardown() {
    generators.clear();
  }

  public void draw(PGraphics graphics) {
    for (SceneGenerator generator : generators) {
      graphics.pushMatrix();
      graphics.translate(generator.getX(), generator.getY());
      generator.draw(graphics);
      graphics.popMatrix();
    }
  }
}
