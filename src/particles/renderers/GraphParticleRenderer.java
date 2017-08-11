package particles.renderers;

import graph.Graph;
import graph.GraphRenderer;
import graph.Node;
import megamu.mesh.Delaunay;
import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 8/10/17.
 */
public class GraphParticleRenderer extends ParticleRenderer {
  private GraphRenderer graphRenderer;

  public GraphParticleRenderer(GraphRenderer graphRenderer) {
    this.graphRenderer = graphRenderer;
  }

  public void setParticles(List<Particle> particles) {

    super.setParticles(particles);
  }

  @Override
  public void render(PGraphics graphics) {
    float[][] points = new float[particles.size()][2];
    Node[] nodes = new Node[particles.size()];
    Graph graph = new Graph();

    for (int i = 0; i < particles.size(); i++) {
      Particle p = particles.get(i);
      points[i][0] = p.position.x;
      points[i][1] = p.position.y;
      nodes[i] = graph.createNode(p.position.x, p.position.y);
    }

    Delaunay delaunay = new Delaunay(points);
    int[][] links = delaunay.getLinks();

    for (int i = 0; i < links.length; i++) {
      graph.createEdge(nodes[links[i][0]], nodes[links[i][1]]);
    }

    graphRenderer.render(graphics, graph);
  }
}
