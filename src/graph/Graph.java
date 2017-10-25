package graph;

import processing.core.PVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by miller on 7/16/17.
 */
public class Graph implements Serializable {
  protected List<Node> nodes;
  protected List<Edge> edges;

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

  public Node createNode(Node n) {
    Node newNode = new Node();
    newNode.position.x = n.position.x;
    newNode.position.y = n.position.y;
    nodes.add(newNode);
    return newNode;
  }

  public Edge createEdge(Node n1, Node n2) {
    Edge e = new Edge(n1, n2);
    edges.add(e);

    n1.connections.add(e);
    n2.connections.add(e);

    return e;
  }

  public Graph copy() {
    Map<Node, Node> oldToNew = new HashMap<>();
    Graph newGraph = new Graph();
    for (Node n : nodes) {
      Node newNode = newGraph.createNode(n.position.x, n.position.y);
      oldToNew.put(n, newNode);
    }

    for (Edge e : edges) {
      Node n1 = oldToNew.get(e.n1);
      Node n2 = oldToNew.get(e.n2);
      newGraph.createEdge(n1, n2);
    }

    return newGraph;
  }

  public PVector getRandomPointOnEdge() {
    Edge e = edges.get((int) Math.floor(Math.random() * edges.size()));

    PVector delta = PVector.sub(e.n1.position, e.n2.position);
    float d = (float) Math.random();
    return new PVector(e.n1.position.x + delta.x * d, e.n1.position.y + delta.y * d);
  }
}
