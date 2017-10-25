package algoplex2;

import graph.Edge;
import graph.Graph;
import graph.Node;
import graph.renderer.AnimatedFillGraphRenderer;
import graph.renderer.BasicGraphRenderer;
import graph.renderer.GraphRenderer;
import processing.core.PGraphics;

/**
 * Created by miller on 10/9/17.
 */
public class NumberTransition {
  private int num;
  private boolean finished;
  private int frame;

  private Graph numberGraph;
  private AnimatedFillGraphRenderer animatedFillGraphRenderer;

  private static final int[][] NUMBERS = {
      {29, 21, 72, 71, 73}, // 1
      {37, 21, 22, 39, 71, 73}, // 2
      {20, 22, 39, 55, 56, 73, 71}, // 3
      {73, 22, 54, 56}, // 4
      {22, 20, 37, 38, 56, 73, 71}, // 5
      {22, 20, 54, 72, 56, 38, 54}, // 6
      {20, 22, 39, 71}, // 7
      {20, 22, 39, 37, 20, 71, 73, 39}, // 8
      {73, 22, 21, 37, 55, 56}, // 9
  };

  public NumberTransition(int num, Grid grid) {
    this.num = num;

    numberGraph = new Graph();

    Node[] nodes = new Node[NUMBERS[num].length];
    for (int i = 0; i < NUMBERS[num].length; i++) {
      nodes[i] = numberGraph.createNode(grid.getNodes().get(NUMBERS[num][i]));
    }



    for (int i = 0; i < nodes.length - 1; i++) {
      numberGraph.createEdge(nodes[i], nodes[i + 1]);
    }

    animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
    animatedFillGraphRenderer.fillSpeed = 40;
    animatedFillGraphRenderer.setThickness(20);
    Edge e = numberGraph.getEdges().get(0);
    animatedFillGraphRenderer.animateEdge(e, e.n1, e.n2);
    animatedFillGraphRenderer.setRestart(false);
  }

  public void draw(PGraphics graphics, Grid grid) {
//    graphics.background(0);

    if (numberGraph.getNodes().size() > 0) {
      animatedFillGraphRenderer.render(graphics, numberGraph);
    }

    frame++;

    if (frame > 100) {
      finished = true;
    }
  }

  public boolean isFinished() {
    return finished;
  }

  public int getNum() {
    return num;
  }
}
