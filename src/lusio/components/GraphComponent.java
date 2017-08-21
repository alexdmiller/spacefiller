package lusio.components;

import graph.*;
import processing.core.PGraphics;
import scene.SceneComponent;

/**
 * Created by miller on 7/16/17.
 */
public class GraphComponent extends SceneComponent {
  private Graph graph;
  private GraphRenderer renderer;

  public GraphComponent(Graph graph, GraphRenderer renderer) {
    this.graph = graph;
    this.renderer = renderer;
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.pushMatrix();
    graphics.translate(getX(), getY());
    renderer.render(graphics, graph);
    graphics.popMatrix();
  }
}
