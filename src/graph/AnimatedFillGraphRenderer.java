package graph;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by miller on 7/16/17.
 */
public class AnimatedFillGraphRenderer implements GraphRenderer {
  private List<AnimationInfo> currentlyAnimating;
  private float fillSpeed = 1f;

  public AnimatedFillGraphRenderer() {
    currentlyAnimating = new ArrayList<>();
  }

  @Override
  public void render(PGraphics graphics, Graph graph) {
    graphics.noFill();
    graphics.strokeWeight(1);
    graphics.stroke(255);

    for (Node n : graph.getNodes()) {
      graphics.ellipse(n.position.x, n.position.y, 20, 20);
    }

    List<Edge> edges = graph.getEdges();
    for (Edge e : edges) {
      graphics.line(e.n1.position.x, e.n1.position.y, e.n2.position.x, e.n2.position.y);
    }

    if (currentlyAnimating.isEmpty()) {
      Edge e = edges.get(0);
      AnimationInfo a = new AnimationInfo(e, e.n1, e.n2);
      currentlyAnimating.add(a);
    }

    graphics.strokeWeight(5);

    List<AnimationInfo> newAnimations = new ArrayList<>();

    Iterator<AnimationInfo> itr = currentlyAnimating.iterator();
    while (itr.hasNext()) {
      AnimationInfo a = itr.next();

      float distance = PVector.dist(a.start.position, a.end.position);
      if (a.fillAmount < distance) {
        a.fillAmount = a.fillAmount + fillSpeed;

        if (a.fillAmount > distance) {
          a.fillAmount = distance;
        }

      } else if (!a.finished) {
        // This block is entered once: right when a line is finished animating.
        // Set the finished flag to true so it doesn't run again.
        a.finished = true;

        // In order to choose the next animated edge, we need to select from
        // available edges that have not already been animated yet. To do this,
        // copy the edges from the end node and then filter them to exclude:
        //
        //   1. The edge that just finished animating
        //   2. Any edges that are in the "currentlyAnimating" list
        //   3. Any edges that are in the "newAnimations" list. This list will be
        //      added to "currentlyAnimating" at the end of the frame.
        List<Edge> connections = new ArrayList<>(a.end.connections);
        connections.remove(a.edge);
        for (AnimationInfo a2 : currentlyAnimating) {
          if (connections.contains(a2.edge) || newAnimations.contains(a2.edge)) {
            connections.remove(a2.edge);
          }
        }

        // Pick a new edge from the filtered list and start animating it.
        for (Edge e : connections) {
          Node start = a.end;
          Node end = a.end != e.n1 ? e.n1 : e.n2;
          AnimationInfo newAnimation = new AnimationInfo(e, start, end);
          newAnimations.add(newAnimation);
        }
      }

      PVector delta = PVector.sub(a.end.position, a.start.position);
      graphics.pushMatrix();
      graphics.translate(a.start.position.x, a.start.position.y);
      graphics.rotate(delta.heading());
      graphics.line(0, 0, a.fillAmount, 0);
      graphics.popMatrix();
    }

    currentlyAnimating.addAll(newAnimations);
  }

  private class AnimationInfo {
    public Node start;
    public Node end;
    public Edge edge;
    public float fillAmount;
    public boolean finished;

    public AnimationInfo(Edge e, Node start, Node end) {
      edge = e;
      this.start = start;
      this.end = end;
    }

    @Override
    public String toString() {
      return start + " -> " + end + " / " + finished;
    }
  }
}
