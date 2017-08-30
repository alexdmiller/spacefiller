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
  private List<Quad> squares;

  private Quad boundingQuad;

  public Grid() {
    super();
    triangles = new ArrayList<>();
    squares = new ArrayList<>();
  }

  public void addTriangle(Node n1, Node n2, Node n3) {
    addTriangle(new Node[] {n1, n2, n3});
  }

  public void addTriangle(Node[] nodes) {
    triangles.add(nodes);
  }

  public void addSquare(Node tl, Node tr, Node br, Node bl, Node center) {
    Quad quad = new Quad(tl, tr, br, bl, center);
    addSquare(quad);
  }

  public void addSquare(Quad quad) {
    squares.add(quad);
  }

  public List<Node[]> getTriangles() {
    return triangles;
  }

  public List<Quad> getSquares() {
    return squares;
  }

  public Quad getBoundingQuad() {
    return boundingQuad;
  }

  public float getWidth() {
    return boundingQuad.getTopRight().position.x - boundingQuad.getTopLeft().position.x;
  }

  public float getHeight() {
    return boundingQuad.getBottomLeft().position.y - boundingQuad.getTopLeft().position.y;
  }

  public void setBoundingQuad(Quad boundingQuad) {
    this.boundingQuad = boundingQuad;
  }
}
