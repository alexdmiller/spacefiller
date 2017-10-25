package common.components;

import particles.Bounds;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.SceneComponent;
import spacefiller.remote.Mod;
import veins.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by miller on 9/25/17.
 */
public class TreeComponent extends SceneComponent {
  @Mod(min=1, max=10)
  public float growthSpeed = 1;

  @Mod(min=10, max=200)
  public float attractorKillRadius = 5;

  @Mod(min=50, max=100)
  public float attractorInfluenceRadius = 100;

  @Mod(min=1, max=5)
  public float edgeThickness = 5;

  @Mod(min=5, max=20)
  public float pulsePeriod = 5;

  @Mod(min=0, max=1000, defaultValue = 10)
  public float pulseLife = 0;

  @Mod(min = 0, max = 1)
  public float foodBrightness = 0;

  private List<PVector> attractors;

  @Mod
  public Tree tree;

  private float activeArea;

  public TreeComponent() {
    attractors = new ArrayList<PVector>();
    tree = new Tree();
  }

  public void addAttractor(PVector pos) {
    synchronized (tree) {
      attractors.add(pos);
    }
  }

  public void remoteRandomAttractor() {
    synchronized (tree) {
      attractors.remove(Math.floor(Math.random() * attractors.size()));
    }
  }

  public void addNode(PVector pos) {
    synchronized (tree) {
      tree.addNode(pos);
    }
  }

  public int numNodes() {
    return tree.getNodes().size();
  }

  public PVector getRandomAttractorPosition() {
    return attractors.get((int) Math.floor(Math.random() * attractors.size()));
  }

  public float activeArea() {
    return activeArea;
  }

  @Override
  public void draw(PGraphics graphics) {
    synchronized (tree) {
      tree.grow(attractors, attractorInfluenceRadius, attractorKillRadius, growthSpeed);
      drawTree(tree, graphics);
      drawAttractors(attractors, graphics);
    }
  }

  void drawTree(Tree tree, PGraphics graphics) {
    graphics.stroke(255);

    activeArea = 0;

    if (!tree.edges.isEmpty()) {
      PVector topLeft = tree.edges.get(0).n1.v.copy();
      PVector bottomRight = tree.edges.get(0).n1.v.copy();

      Iterator<Tree.Edge> edges = tree.edges.iterator();

      while (edges.hasNext()) {
        Tree.Edge edge = edges.next();

        topLeft.x = Math.min(Math.min(edge.n1.v.x, edge.n2.v.x), topLeft.x);
        topLeft.y = Math.min(Math.min(edge.n1.v.y, edge.n2.v.y), topLeft.y);
        bottomRight.x = Math.max(Math.max(edge.n1.v.x, edge.n2.v.x), bottomRight.x);
        bottomRight.y = Math.max(Math.max(edge.n1.v.y, edge.n2.v.y), bottomRight.y);

        if (pulseLife > 0 && edge.age >= pulseLife) {
          edges.remove();
        } else {
          float w = edgeThickness * ageToThickness(edge.age) + 1;
          if (w > 0) {
            graphics.strokeWeight(w);
            graphics.line(edge.n1.v.x, edge.n1.v.y, edge.n2.v.x, edge.n2.v.y);
          }
          edge.age++;
        }
      }

      activeArea = (bottomRight.x - topLeft.x) * (bottomRight.y - topLeft.y);
    }

    Iterator<Tree.Node> nodes = tree.nodes.iterator();
    while (nodes.hasNext()) {
      Tree.Node node = nodes.next();
      if (node.age >= pulseLife) {
        nodes.remove();
      } else {
        node.age++;
      }
    }
  }

  void drawAttractors(List<PVector> attractors, PGraphics graphics) {
    graphics.strokeWeight(2);
    for (PVector attractor : attractors) {
      graphics.stroke(255, foodBrightness * 255);
      graphics.point(attractor.x, attractor.y);
    }
  }

  float ageToThickness(int age) {
    return (float) Math.max(0, Math.sin(age * (Math.PI / pulsePeriod)));
  }

  public int numAttractors() {
    return attractors.size();
  }

  public void clearNodes() {
    tree.nodes.clear();
  }
}
