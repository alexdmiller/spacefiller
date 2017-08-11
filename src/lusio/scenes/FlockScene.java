package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.DelaunayRenderer;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.Arrays;
import java.util.Map;

public class FlockScene extends Scene {
  ParticleGenerator particleGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer particleWebRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(50, new Bounds(Lusio.HEIGHT), 3);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);

    // particleGenerator.addRenderer(new DelaunayRenderer(2));
    particleGenerator.addRenderer(new ParticleDotRenderer(10));
    particleWebRenderer = new ParticleWebRenderer(100, 4);
    particleGenerator.addRenderer(particleWebRenderer);
    particleGenerator.getParticleSystem().setMaxParticles(300);
    particleGenerator.getParticleSystem().createSource(0, 0, 1, 3);

    flockParticles = new FlockParticles(1, 0.5f, 0.5f, 40, 100, 200, 1f, 5);
    particleGenerator.addBehavior(flockParticles);
    addGenerator(particleGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    float[] euler = cube.getNormalizedEuler();

    particleGenerator.setRotation(cube.getQuaternion());
    flockParticles.setMaxSpeed(cube.getRotationalVelocity() * 10 + 5);
    flockParticles.setDesiredSeparation(cube.getRotationalVelocity() * 1 + 50);
    particleWebRenderer.setLineThreshold(Math.min(cube.getRotationalVelocity() * 10, 100) + 60);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
