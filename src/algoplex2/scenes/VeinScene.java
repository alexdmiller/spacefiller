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

  @Override
  public void preSetup(Grid grid) {
    treeComponent = new TreeComponent();

    treeComponent.attractorInfluenceRadius = 40;
    treeComponent.pulseLife = 40;
    treeComponent.pulsePeriod = 10;

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


  @Override
  public void draw(PGraphics graphics) {
    super.draw(graphics);

    // System.out.println();
    if (treeComponent.activeArea() < 3000) {
      for (int i = 0; i < 100; i++) {
        treeComponent.addAttractor(grid.getRandomPointOnEdge());
      }

      if (treeComponent.numNodes() != 0) {
        treeComponent.clearNodes();
      }
      for (int i = 0; i < 3; i++) {
        treeComponent.addNode(treeComponent.getRandomAttractorPosition());
      }
    } else {
      for (int i = 0; i < 5; i++) {
//        treeComponent.addAttractor(grid.getRandomPointOnEdge());
//        treeComponent.remoteRandomAttractor();
      }
    }
  }

  @Override
  public void teardown() {

  }
}
