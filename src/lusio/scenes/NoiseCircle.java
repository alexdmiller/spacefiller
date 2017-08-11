package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import lusio.generators.PerlinFlowGenerator;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class NoiseCircle extends Scene {
  PerlinFlowGenerator perlinFlowGenerator;

  @Override
  public void setup(Map<String, Graph> graphs) {
    perlinFlowGenerator = new PerlinFlowGenerator(new Bounds(1000));
    perlinFlowGenerator.setPos(900, 500);
    perlinFlowGenerator.setFallSpeed(0.5f);
    perlinFlowGenerator.setNumPoints(100);
    perlinFlowGenerator.setLineSparsity(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(3);
    addGenerator(perlinFlowGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    perlinFlowGenerator.setFlowForce(Math.abs(cube.getEulerRotation()[1]) * 5);
    perlinFlowGenerator.setNoiseScale(1000 - Math.abs(cube.getEulerRotation()[2]) * 200);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setNoiseSpeed2(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setLineLength(cube.getFlipAmount() * 100 + 100);
    perlinFlowGenerator.setFallSpeed(Math.abs(cube.getEulerRotation()[0]));
    perlinFlowGenerator.setCircleRadius(200 + cube.getFlipAmount() * 400);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
