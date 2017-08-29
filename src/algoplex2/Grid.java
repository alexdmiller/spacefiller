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
    triangles.add(new Node[] {n1, n2, n3});
  }

  public void addSquare(Node tl, Node tr, Node br, Node bl, Node center) {
    Quad quad = new Quad(tl.position, tr.position, br.position, bl.position);
    quad.setCenter(center.position);
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
    return boundingQuad.getTopRight().x - boundingQuad.getTopLeft().x;
  }

  public float getHeight() {
    return boundingQuad.getBottomLeft().y - boundingQuad.getTopLeft().y;
  }

  public void setBoundingQuad(Quad boundingQuad) {
    this.boundingQuad = boundingQuad;
  }

  public Grid copy() {
    Graph newGraph = super.copy();
    Grid newGrid = new Grid();

    newGrid.nodes = newGraph.getNodes();
    newGrid.edges = newGraph.getEdges();

    for (Node[] triangle : triangles) {

    }

    return newGrid;
  }
}
