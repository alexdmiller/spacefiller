package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.Map;

public class FlockScene extends Scene {
  ParticleGenerator particleGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer particleWebRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(200, new Bounds(Lusio.WIDTH, Lusio.HEIGHT), 2);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    particleGenerator.addRenderer(new ParticleWormRenderer(20, 2));

    particleWebRenderer = new ParticleWebRenderer(50, 2);
    particleGenerator.addRenderer(particleWebRenderer);

    particleGenerator.addRenderer(new ParticleDotRenderer(10));

    particleGenerator.getParticleSystem().createSource(0, 0, 1);

    flockParticles = new FlockParticles(1, 0.5f, 0.5f, 20, 100, 100, 1f, 5);

    particleGenerator.addBehavior(flockParticles);
    addGenerator(particleGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    flockParticles.setDesiredSeparation(cube.getRotationalVelocity() + 20);
    flockParticles.setMaxSpeed(cube.getRotationalVelocity());
    particleWebRenderer.setLineThreshold(cube.getRotationalVelocity() * 3 + 20);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
