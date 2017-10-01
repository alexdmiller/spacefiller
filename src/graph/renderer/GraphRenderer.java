package graph.renderer;

import graph.Graph;
import processing.core.PGraphics;

public interface GraphRenderer {
  void render(PGraphics graphics, Graph graph);
}
