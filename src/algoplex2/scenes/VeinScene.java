package algoplex2.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import common.components.TreeComponent;
import graph.*;
import processing.core.PVector;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;
import veins.Tree;

import java.util.List;

public class VeinScene extends GridScene {
  @Mod
  public TreeComponent treeComponent;

  public VeinScene() {

  }

  @Override
  public void preSetup(Grid grid) {
    treeComponent = new TreeComponent();

    treeComponent.attractorInfluenceRadius = 30;
    treeComponent.pulseLife = 100;
    treeComponent.pulsePeriod = 20;

    for (int i = 0; i < 1000; i++) {
      treeComponent.addAttractor(grid.getRandomPointOnEdge());
    }

    for (int i = 0; i < 10; i++) {
      treeComponent.addNode(new PVector(
          (float) Math.random() * grid.getWidth(), (float) Math.random() * grid.getHeight()));
    }

    addComponent(treeComponent);

    super.preSetup(grid);
  }

  @Mod
  public void addStuff() {
    if (treeComponent.numAttractors() < 500) {
      for (int i = 0; i < 30; i++) {
        treeComponent.addAttractor(grid.getRandomPointOnEdge());
      }
    }

    if (treeComponent.numNodes() < 5) {
      for (int i = 0; i < 1; i++) {
        treeComponent.addNode(new PVector(
            (float) Math.random() * grid.getWidth(), (float) Math.random() * grid.getHeight()));
      }
    }
  }

  @Override
  public void draw(PGraphics graphics) {
//    System.out.println("nodes = " + treeComponent.numNodes());
//    System.out.println("attractors = " + treeComponent.numAttractors());
    super.draw(graphics);

    // System.out.println();
    if (treeComponent.activeArea() < 3000) {
      for (int i = 0; i < 500; i++) {
        treeComponent.addAttractor(grid.getRandomPointOnEdge());
      }

      if (treeComponent.numNodes() != 0) {
        treeComponent.clearNodes();
      }
      for (int i = 0; i < 1; i++) {
        treeComponent.addNode(treeComponent.getRandomAttractorPosition());
      }
    }
  }

  @Override
  public void teardown() {

  }
}
