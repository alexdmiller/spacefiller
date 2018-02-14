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

  @Override
  public void setup() {
    perlinFlow = new PerlinFlowComponent(new Bounds(width, height));
    perlinFlow.setColorProvider(ConstantColorProvider.WHITE);
    perlinFlow.setPos(width / 2, height / 2);
    perlinFlow.setFallSpeed(0);
    perlinFlow.setNoiseSpeed1(0.01f);
    perlinFlow.setNoiseSpeed2(0);
    perlinFlow.setFlowForce(10);
    perlinFlow.setLineLength(20);
    perlinFlow.setScrollSpeed(0.1f);
    perlinFlow.setMainSpeed(0.01f);
    addComponent(perlinFlow);

    super.setup();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += updateSpeed;
    super.draw(graphics);
  }
}
