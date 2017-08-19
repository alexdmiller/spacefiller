package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.*;
import particles.Bounds;
import particles.behaviors.AttractParticles;
import particles.behaviors.FlockParticles;
import particles.behaviors.ParticleFriction;
import particles.behaviors.RepelParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import scene.Scene;
import toxi.geom.Quaternion;

import java.util.Map;

public class ThreeDeeFlockScene extends LusioScene {
  private ParticleComponent particleGenerator;
  private FlockParticles flockParticles;
  private ParticleWebRenderer particleWebRenderer;

  private ParticleComponent particleGenerator2;
  private RepelParticles repelBehavior;

  private float speed;


  @Override
  public void setup() {
    particleGenerator2 = new ParticleComponent(200, new Bounds(Lusio.HEIGHT), 3);
    particleGenerator2.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    particleGenerator2.addRenderer(new ParticleDotRenderer(10, Lusio.instance));
    particleGenerator2.addRenderer(new ParticleWormRenderer(20, 5, Lusio.instance));
    repelBehavior = new RepelParticles(50, 2);
    particleGenerator2.addBehavior(repelBehavior);
    particleGenerator2.addBehavior(new AttractParticles(100, 0.5f));
    particleGenerator2.addBehavior(new ParticleFriction(0.9f));
    addComponent(particleGenerator2);
  }

  @Override
  public void draw(PGraphics graphics) {
    particleGenerator2.setRotation(cube.getQuaternion().multiply(Quaternion.createFromEuler(-1, -1, -1)));

    speed += cube.getRotationalVelocity();
    speed *= 0.9;

    repelBehavior.setRepelThreshold(cube.getRotationalVelocity() * 100);

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
