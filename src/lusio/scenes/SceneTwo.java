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

public class SceneTwo extends Scene {
  ParticleGenerator particleGenerator;
  PerlinFlowGenerator perlinFlowGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer webRenderer;
  ParticleDotRenderer dotRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(70, new Bounds(300));
    particleGenerator.setPos(900, 500);
    webRenderer = new ParticleWebRenderer(50, 2);
    particleGenerator.addRenderer(webRenderer);
    dotRenderer = new ParticleDotRenderer(10);
    particleGenerator.addRenderer(dotRenderer);
    flockParticles = new FlockParticles(1, 0.5f, 1f, 30, 50, 70, 2, 10);
    particleGenerator.addBehavior(flockParticles);
    addGenerator(particleGenerator);

    perlinFlowGenerator = new PerlinFlowGenerator(new Bounds(1000));
    perlinFlowGenerator.setPos(900, 500);
    perlinFlowGenerator.setFallSpeed(0.5f);
    perlinFlowGenerator.setNumPoints(100);
    perlinFlowGenerator.setLineSparsity(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(2);
    addGenerator(perlinFlowGenerator);

    Graph graph = graphs.get("window");
    if (graph != null) {
      GraphGenerator graphGen = new GraphGenerator(graph, new SinGraphRenderer());
      addGenerator(graphGen);
    }
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    particleGenerator.setRotation(cube.getQuaternion());
    webRenderer.setLineThreshold(cube.getFlipAmount() * 200 + 10);
    flockParticles.setDesiredSeparation(cube.getFlipAmount() * 100 + 30);
    dotRenderer.setDotSize(cube.getFlipAmount() * 10 + 10);

    perlinFlowGenerator.setFlowForce(Math.abs(cube.getEulerRotation()[1]) * 5);
    perlinFlowGenerator.setNoiseScale(1000 - Math.abs(cube.getEulerRotation()[2]) * 200);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setNoiseSpeed2(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setLineLength(cube.getFlipAmount() * 100 + 20);
    perlinFlowGenerator.setFallSpeed(Math.abs(cube.getEulerRotation()[0]));
    perlinFlowGenerator.setCircleRadius(200 + cube.getFlipAmount() * 400);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
