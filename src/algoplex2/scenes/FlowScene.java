package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Grid;
import algoplex2.Quad;
import common.color.ConstantColorProvider;
import graph.Node;
import lusio.Lusio;
import lusio.components.PerlinFlowComponent;
import particles.Bounds;
import spacefiller.remote.Mod;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class FlowScene extends GridScene {
  private float t;

  @Mod
  PerlinFlowComponent perlinFlowGenerator;


  @Mod(min = 0, max = 1)
  public float updateSpeed = 0.01f;

  @Override
  public void preSetup(Grid grid) {
    perlinFlowGenerator = new PerlinFlowComponent(new Bounds(grid.getWidth() * 2, grid.getHeight() * 2));
    perlinFlowGenerator.setColorProvider(ConstantColorProvider.WHITE);
    perlinFlowGenerator.setPos(grid.getWidth() / 2, grid.getHeight() / 2);
    perlinFlowGenerator.setFallSpeed(2);
    perlinFlowGenerator.setNumPoints(200);
    perlinFlowGenerator.setLineSparsity(1f);
    perlinFlowGenerator.setScrollSpeed(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(3);
    perlinFlowGenerator.setInterpolation(1);
    perlinFlowGenerator.setNoiseSpeed1(0.00f);
    perlinFlowGenerator.setNoiseSpeed2(0.001f);
    perlinFlowGenerator.setNoiseScale(500);
    perlinFlowGenerator.setMainSpeed(0.05f);
    perlinFlowGenerator.setScrambleSpeed(0);
    perlinFlowGenerator.setLineLength(20);
    perlinFlowGenerator.setSnapToGrid(true);
    perlinFlowGenerator.setGridResolution(grid.getColumns() * 4);
    addComponent(perlinFlowGenerator);

    super.preSetup(grid);
  }

  @Override
  public void draw(PGraphics graphics) {
    t += updateSpeed;

    super.draw(graphics);
  }
}
