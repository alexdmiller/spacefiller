package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/16/17.
 */
public class Graph {
  private List<Node> nodes;
  private List<Edge> edges;

  public Graph() {
    nodes = new ArrayList<>();
    edges = new ArrayList<>();
  }

  public List<Node> getNodes() {
    return nodes;
  }

  public List<Edge> getEdges() {
    return edges;
  }

  public Node createNode(float x, float y) {
    Node n = new Node();
    n.position.x = x;
    n.position.y = y;
    nodes.add(n);
    return n;
  }

  public Edge createEdge(Node n1, Node n2) {
    Edge e = new Edge(n1, n2);
    edges.add(e);

    n1.connections.add(e);
    n2.connections.add(e);

    return e;
  }
}
