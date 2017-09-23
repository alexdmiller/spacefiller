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
    perlinFlowGenerator = new PerlinFlowComponent(new Bounds(Lusio.WIDTH * 2, Lusio.HEIGHT * 2));
    perlinFlowGenerator.setColorProvider(ConstantColorProvider.WHITE);
    perlinFlowGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    perlinFlowGenerator.setFallSpeed(0);
    perlinFlowGenerator.setNumPoints(300);
    perlinFlowGenerator.setLineSparsity(0f);
    perlinFlowGenerator.setScrollSpeed(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(6);
    perlinFlowGenerator.setInterpolation(1);
    perlinFlowGenerator.setNoiseSpeed1(0);
    perlinFlowGenerator.setNoiseSpeed2(0);
    perlinFlowGenerator.setNoiseScale(1000);
    perlinFlowGenerator.setMainSpeed(0.05f);
    perlinFlowGenerator.setScrambleSpeed(0.0001f);
    perlinFlowGenerator.setLineLength(50);
    addComponent(perlinFlowGenerator);

    super.preSetup(grid);
  }

  @Override
  public void draw(PGraphics graphics) {
    t += updateSpeed;

    super.draw(graphics);
  }
}
