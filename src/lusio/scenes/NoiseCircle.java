package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.PerlinFlowComponent;
import particles.Bounds;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class NoiseCircle extends LusioScene {
  PerlinFlowComponent perlinFlowGenerator;

  @Override
  public void setup() {
    perlinFlowGenerator = new PerlinFlowComponent(new Bounds(1000));
    perlinFlowGenerator.setPos(900, 500);
    perlinFlowGenerator.setFallSpeed(0.5f);
    perlinFlowGenerator.setNumPoints(100);
    perlinFlowGenerator.setLineSparsity(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(6);
    perlinFlowGenerator.setColorProvider(Lusio.instance);
    addComponent(perlinFlowGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    perlinFlowGenerator.setFlowForce(Math.abs(cube.getEulerRotation()[1]) * 5);
    perlinFlowGenerator.setNoiseScale(1000 - Math.abs(cube.getEulerRotation()[2]) * 200);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setNoiseSpeed2(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setLineLength(cube.getCounter() * 100 + 100);
    perlinFlowGenerator.setFallSpeed(Math.abs(cube.getEulerRotation()[0]));
    perlinFlowGenerator.setCircleRadius(200 + cube.getCounter() * 400);

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
