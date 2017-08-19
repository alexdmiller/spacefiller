package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.*;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class FlockScene extends LusioScene {
  ParticleComponent particleGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer particleWebRenderer;

  @Override
  public void setup() {
    particleGenerator = new ParticleComponent(100, new Bounds(Lusio.HEIGHT), 3);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);

    // particleGenerator.addRenderer(new DelaunayRenderer(2));
    particleGenerator.addRenderer(new ParticleDotRenderer(10, Lusio.instance));
    particleWebRenderer = new ParticleWebRenderer(100, 4);
    particleWebRenderer.setColorProvider(Lusio.instance);
    particleGenerator.addRenderer(particleWebRenderer);
    particleGenerator.getParticleSystem().setMaxParticles(300);
    particleGenerator.getParticleSystem().createSource(0, 0, 1, 3);

    flockParticles = new FlockParticles(1, 0.5f, 0.5f, 40, 100, 200, 1f, 5);
    particleGenerator.addBehavior(flockParticles);
    addComponent(particleGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    float[] euler = cube.getNormalizedEuler();

    particleGenerator.setRotation(cube.getQuaternion());
    flockParticles.setMaxSpeed(cube.getRotationalVelocity() * 10 + 5);
    flockParticles.setDesiredSeparation(cube.getRotationalVelocity() * 1 + 50);
    particleWebRenderer.setLineThreshold(Math.min(cube.getRotationalVelocity() * 10, 100) + 60);

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
