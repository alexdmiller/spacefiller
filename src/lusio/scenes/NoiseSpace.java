package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.PerlinFlowComponent;
import particles.Bounds;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class NoiseSpace extends LusioScene {
  PerlinFlowComponent perlinFlowGenerator;
  private float scrollSpeed;


  @Override
  public void setup() {
    perlinFlowGenerator = new PerlinFlowComponent(new Bounds(Lusio.WIDTH * 2, Lusio.HEIGHT * 2));
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
    perlinFlowGenerator.setColorProvider(Lusio.instance);
    addComponent(perlinFlowGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    scrollSpeed += cube.getFlipAmount() / 10;
    scrollSpeed *= 0.9f;

    float[] euler = cube.getEulerRotation();

    perlinFlowGenerator.setScrollSpeed(scrollSpeed);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 5000);
    perlinFlowGenerator.setNoiseSpeed2(euler[0] / 1000);
    perlinFlowGenerator.setFlowForce(scrollSpeed * 10 + 2);

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
