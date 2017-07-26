package lusio.scenes;

import graph.Graph;
import graph.PipeGraphRenderer;
import lusio.Lightcube;
import lusio.generators.ContourGenerator;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.behaviors.JitterParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class SceneOne extends Scene {
  ParticleGenerator particleGenerator;
  JitterParticles jitterParticles;
  FlockParticles flockParticles;
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
    // addGenerator(particleGenerator);

    particleGenerator2 = new ParticleGenerator(50, new Bounds(300));
    particleGenerator2.setPos(400, 400);
    particleGenerator2.addRenderer(new ParticleDotRenderer(1));
    particleGenerator2.addRenderer(new ParticleWebRenderer(100, 1));
    particleGenerator2.addBehavior(jitterParticles);
    flockParticles = new FlockParticles(1, 2, 0.5f, 50, 200, 100, 1, 10);
    particleGenerator2.addBehavior(flockParticles);
    addGenerator(particleGenerator2);

    Graph graph = graphs.get("window");
    if (graph != null) {
      GraphGenerator graphGen = new GraphGenerator(graph, new PipeGraphRenderer());
      addGenerator(graphGen);
    }

    contourGenerator = new ContourGenerator(new Bounds(500));
    contourGenerator.setPos(1000, 500);
    addGenerator(contourGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    particleGenerator.setRotation(cube.getQuaternion());
    particleGenerator2.setRotation(cube.getQuaternion());
    contourGenerator.setRotation(cube.getQuaternion());

    flockParticles.setMaxSpeed(Math.max(cube.getRotationalVelocity() + 0.5f, cube.getFlipAmount() * 50));
    flockParticles.setDesiredSeparation(Math.max(cube.getRotationalVelocity() + 20, cube.getFlipAmount() * 150));

    // contourGenerator.setUpdateSpeed(cube.getRotationalVelocity() / 10f + 0.01f);
    contourGenerator.setNoiseAmplitude(cube.getRotationalVelocity() * 10 + 10);
    contourGenerator.setXSpeed(cube.getRotationalVelocity() / 100f);

    super.draw(cube, graphics);
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
