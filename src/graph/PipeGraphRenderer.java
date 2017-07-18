package graph;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by miller on 7/16/17.
 */
public class PipeGraphRenderer implements GraphRenderer {
  private List<AnimationInfo> currentlyAnimating;
  private float chance = 0.5f;
  private int maxPerEdge = 10;

  public PipeGraphRenderer() {
    currentlyAnimating = new ArrayList<>();
  }

  @Override
  public void render(PGraphics graphics, Graph graph) {
    graphics.noFill();
    graphics.strokeWeight(1);
    graphics.stroke(255);

    List<Edge> edges = graph.getEdges();

    float maxPoints = maxPerEdge * edges.size();

    if (currentlyAnimating.size() < maxPoints) {
      Edge e = edges.get((int) Math.floor(edges.size() * Math.random()));
      AnimationInfo a = new AnimationInfo(e, e.n1, e.n2, (float) Math.random() * 3 + 1);
      currentlyAnimating.add(a);
    }

    graphics.strokeWeight(1);

    List<AnimationInfo> newAnimations = new ArrayList<>();

    Iterator<AnimationInfo> itr = currentlyAnimating.iterator();
    while (itr.hasNext()) {
      AnimationInfo a = itr.next();

      float distance = PVector.dist(a.start.position, a.end.position);
      if (a.point < distance) {
        a.point = a.point + a.speed;

        if (a.point > distance) {
          a.point = distance;
        }

      } else if (!a.finished) {
        // This block is entered once: right when a line is finished animating.
        // Set the finished flag to true so it doesn't run again.
        a.finished = true;

        itr.remove();

        // In order to choose the next animated edge, we need to select from
        // available edges that have not already been animated yet. To do this,
        // copy the edges from the end node and then filter them to exclude:
        //
        //   1. The edge that just finished animating
        //   2. Any edges that are in the "currentlyAnimating" list
        //   3. Any edges that are in the "newAnimations" list. This list will be
        //      added to "currentlyAnimating" at the end of the frame.
        List<Edge> connections = new ArrayList<>(a.end.connections);

        // Pick a new edge from the filtered list and start animating it.
        Edge e = connections.get((int) Math.floor(connections.size() * Math.random()));
        if (edges.size() < maxPoints) {
          Node start = a.end;
          Node end = a.end != e.n1 ? e.n1 : e.n2;
          AnimationInfo newAnimation = new AnimationInfo(e, start, end, (float) Math.random() * 3 + 1);
          newAnimations.add(newAnimation);
        }
      }

      graphics.strokeWeight(3);
      PVector delta = PVector.sub(a.end.position, a.start.position);
      graphics.pushMatrix();
      graphics.translate(a.start.position.x, a.start.position.y);
      graphics.rotate(delta.heading());
      graphics.point(a.point, (float) (Math.sin(a.point / 10 + a.shift) * 3));
      graphics.popMatrix();
    }

    currentlyAnimating.addAll(newAnimations);
  }

  private class AnimationInfo {
    public Node start;
    public Node end;
    public Edge edge;
    public float point;
    public float speed;
    public float shift;
    boolean finished;

    public AnimationInfo(Edge e, Node start, Node end, float speed) {
      edge = e;
      this.start = start;
      this.end = end;
      this.speed = speed;
      this.shift = (float) (Math.random() * 2 * Math.PI);
    }
  }
}