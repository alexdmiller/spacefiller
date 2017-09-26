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
    super.draw(graphics);
  }

  @Override
  public void teardown() {

  }
}
