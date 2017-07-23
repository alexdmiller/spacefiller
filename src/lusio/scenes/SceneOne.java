package lusio.scenes;

import graph.AnimatedFillGraphRenderer;
import graph.Graph;
import graph.PipeGraphRenderer;
import lusio.generators.ContourGenerator;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import modulation.Mod;
import particles.Bounds;
import particles.Particle;
import particles.behaviors.FlockParticles;
import particles.behaviors.JitterParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;
import toxi.geom.Quaternion;

import java.util.Map;

public class SceneOne extends Scene {
  ParticleGenerator particleGenerator;
  JitterParticles jitterParticles;
  ParticleGenerator particleGenerator2;
  ContourGenerator contourGenerator;

  private float transitionOutTimer;
  private float transitionDuration = 20;

  @Override
  public void setup(Map<String, Graph> graphs) {
    jitterParticles = new JitterParticles(0.1f);

    particleGenerator = new ParticleGenerator(50, new Bounds(100));
    particleGenerator.setPos(500, 500);
    particleGenerator.addRenderer(new ParticleDotRenderer(2));
    particleGenerator.addBehavior(jitterParticles);
    addGenerator(particleGenerator);

    particleGenerator2 = new ParticleGenerator(50, new Bounds(300));
    particleGenerator2.setPos(800, 300);
    particleGenerator2.addRenderer(new ParticleDotRenderer(1));
    particleGenerator2.addRenderer(new ParticleWebRenderer(100, 1));
    particleGenerator2.addBehavior(jitterParticles);
    particleGenerator2.addBehavior(
        new FlockParticles(1, 1, 0.5f, 20, 100, 100, 1, 10));
    addGenerator(particleGenerator2);

    Graph graph = graphs.get("window");
    GraphGenerator graphGen = new GraphGenerator(graph, new PipeGraphRenderer());
    addGenerator(graphGen);

    ContourGenerator contourGenerator = new ContourGenerator();
    contourGenerator.setPos(400, 400);
    addGenerator(contourGenerator);
  }

  @Override
  public void draw(Quaternion quaternion, PGraphics graphics) {
    particleGenerator.setRotation(quaternion.getConjugate());
    particleGenerator2.setRotation(quaternion);

    super.draw(quaternion, graphics);
  }

  @Override
  public boolean transitionOut() {
    transitionOutTimer++;

    if (transitionOutTimer > transitionDuration) {
      transitionOutTimer = 0;
      return true;
    }

    Bounds bounds = particleGenerator.getBounds();
    bounds.setSize(bounds.getWidth() + 20);

    return false;
  }
}
