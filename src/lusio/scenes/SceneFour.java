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

public class SceneFour extends Scene {
  ParticleGenerator particleGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer particleWebRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(300, new Bounds(Lusio.WIDTH, Lusio.HEIGHT), 2);
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
    // graphics.stroke(cube.getColor());

    // particleGenerator.setRotation(Quaternion.createFromEuler(0f, (float) Math.PI/ 2, 0));
    flockParticles.setDesiredSeparation(cube.getRotationalVelocity() * 5 + 20);
    particleWebRenderer.setLineThreshold(cube.getRotationalVelocity() * 10 + 20);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
