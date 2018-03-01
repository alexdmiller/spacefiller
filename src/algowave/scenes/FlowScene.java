package algowave.scenes;

import common.color.ConstantColorProvider;
import lusio.components.PerlinFlowComponent;
import particles.Bounds;
import processing.core.PGraphics;
import scene.Scene;
import spacefiller.remote.Mod;

public class FlowScene extends Scene {
  private float t;

  @Mod
  public PerlinFlowComponent perlinFlow;

  @Mod(min = 0, max = 1)
  public float updateSpeed = 0.01f;

  public FlowScene() {
    perlinFlow = new PerlinFlowComponent(new Bounds(width, height));
  }

  @Override
  public void setup() {
    perlinFlow.setBounds(new Bounds(width, height));
    perlinFlow.setColorProvider(ConstantColorProvider.WHITE);
    perlinFlow.setPos(width / 2, height / 2);
    perlinFlow.setSnapToGrid(true);
    perlinFlow.setGridCellSize(20);
    perlinFlow.setFallSpeed(-10);
    perlinFlow.setNoiseSpeed1(0.001f);
    perlinFlow.setNoiseSpeed2(0.001f);
    perlinFlow.setFlowForce(1);
    perlinFlow.setLineLength(5);
    perlinFlow.setScrollSpeed(0.1f);
    perlinFlow.setMainSpeed(0.01f);
    perlinFlow.setInterpolation(1);
    perlinFlow.setLineSparsity(1f);
    perlinFlow.setLineThickness(1);
    perlinFlow.setNoiseScale(1000);
    addComponent(perlinFlow);

    super.setup();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += updateSpeed;
    super.draw(graphics);
  }
}
