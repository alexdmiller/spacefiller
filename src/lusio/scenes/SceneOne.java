package lusio.scenes;

import graph.Graph;

import java.util.Map;

public class SceneOne extends Scene {
  @Override
  public void setup(Map<String, Graph> graphs) {
    Graph graph = graphs.get("window");
  }

  @Override
  public void draw() {

  }
}
