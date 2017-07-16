package lusio.generators;

import graph.*;
import processing.core.PGraphics;

/**
 * Created by miller on 7/16/17.
 */
public class GraphGenerator extends SceneGenerator {
  private Graph graph;
  private GraphRenderer renderer;

  public GraphGenerator() {
    graph = new Graph();
    renderer = new AnimatedFillGraphRenderer();

    Node n1 = graph.createNode(100, 100);
    Node n2 = graph.createNode(200, 100);
    Node n3 = graph.createNode(200, 200);
    Node n4 = graph.createNode(400, 200);

    graph.createEdge(n1, n2);
    graph.createEdge(n2, n3);
    graph.createEdge(n3, n1);
    graph.createEdge(n4, n1);
    graph.createEdge(n4, n2);
  }

  @Override
  public void draw(PGraphics graphics) {
    renderer.render(graphics, graph);
  }
}
