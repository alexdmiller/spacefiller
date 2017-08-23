package algoplex2;

import graph.Edge;
import graph.Graph;
import graph.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 8/22/17.
 */
public class Grid extends Graph {
  private List<Node[]> triangles;
  private List<Node[]> squares;

  private Quad boundingQuad;

  public Grid() {
    super();
    triangles = new ArrayList<>();
    squares = new ArrayList<>();
  }

  public void addTriangle(Node n1, Node n2, Node n3) {
    triangles.add(new Node[] {n1, n2, n3});
  }

  public void addSquare(Node tl, Node tr, Node br, Node bl) {
    squares.add(new Node[] {tl, tr, br, bl});
  }

  public List<Node[]> getTriangles() {
    return triangles;
  }

  public List<Node[]> getSquares() {
    return squares;
  }

  public Quad getBoundingQuad() {
    return boundingQuad;
  }

  public void setBoundingQuad(Quad boundingQuad) {
    this.boundingQuad = boundingQuad;
  }
}
