package lusio.generators;

import graph.*;
import processing.core.PGraphics;

/**
 * Created by miller on 7/16/17.
 */
public class GraphGenerator extends SceneGenerator {
  private Graph graph;
  private GraphRenderer renderer;

  public GraphGenerator(Graph graph, GraphRenderer renderer) {
    this.graph = graph;
    this.renderer = renderer;
  }

  @Override
  public void draw(PGraphics graphics) {
    renderer.render(graphics, graph);
  }
}
