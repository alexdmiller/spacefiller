package graph;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Node {
  public PVector position;
  public List<Edge> connections;

  public Node() {
    position = new PVector();
    connections = new ArrayList<>();
  }

  @Override
  public String toString() {
    return position.toString();
  }
}
