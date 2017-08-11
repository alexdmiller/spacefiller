package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import particles.Bounds;
import particles.Particle;
import particles.behaviors.AttractParticles;
import particles.behaviors.FlockParticles;
import particles.behaviors.ParticleFriction;
import particles.behaviors.RepelParticles;
import particles.renderers.DelaunayRenderer;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.Arrays;
import java.util.Map;

public class ParticleCube extends Scene {
  private ParticleGenerator particleGenerator;
  private RepelParticles repelBehavior;

  private float speed;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(200, new Bounds(Lusio.HEIGHT/2), 3);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    particleGenerator.addRenderer(new ParticleDotRenderer(10));
    particleGenerator.addRenderer(new ParticleWormRenderer(10, 3));
    repelBehavior = new RepelParticles(50, 2);
    particleGenerator.addBehavior(repelBehavior);
    particleGenerator.addBehavior(new AttractParticles(100, 0.5f));
    particleGenerator.addBehavior(new ParticleFriction(0.9f));
    addGenerator(particleGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    particleGenerator.setRotation(cube.getQuaternion());
    repelBehavior.setRepelThreshold(cube.getRotationalVelocity() * 100 + 10);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
