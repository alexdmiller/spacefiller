package graph;

/**
 * Created by miller on 7/16/17.
 */
public class Edge {
  public Node n1;
  public Node n2;

  public Edge(Node n1, Node n2) {
    this.n1 = n1;
    this.n2 = n2;
  }

  @Override
  public String toString() {
    return n1.toString() + ", " + n2.toString();
  }
}
