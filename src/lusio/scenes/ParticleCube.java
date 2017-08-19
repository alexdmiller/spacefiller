package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.*;
import particles.Bounds;
import particles.behaviors.AttractParticles;
import particles.behaviors.ParticleFriction;
import particles.behaviors.RepelParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class ParticleCube extends LusioScene {
  private ParticleComponent particleGenerator;
  private RepelParticles repelBehavior;

  private float speed;

  @Override
  public void setup() {
    particleGenerator = new ParticleComponent(200, new Bounds(Lusio.HEIGHT/2), 3);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    particleGenerator.addRenderer(new ParticleDotRenderer(10, Lusio.instance));
    particleGenerator.addRenderer(new ParticleWormRenderer(10, 3, Lusio.instance));
    repelBehavior = new RepelParticles(50, 2);
    particleGenerator.addBehavior(repelBehavior);
    particleGenerator.addBehavior(new AttractParticles(100, 0.5f));
    particleGenerator.addBehavior(new ParticleFriction(0.9f));
    addComponent(particleGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    particleGenerator.setRotation(cube.getQuaternion());
    repelBehavior.setRepelThreshold(cube.getRotationalVelocity() * 100 + 10);

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
