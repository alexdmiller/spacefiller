package lusio.generators;

import particles.Bounds;
import particles.Particle;
import particles.ParticleSystem;
import particles.behaviors.ParticleBehavior;
import particles.renderers.ParticleRenderer;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator extends SceneGenerator {
  private ParticleSystem particleSystem;
  private int numParticles;
  private List<ParticleRenderer> renderers;
  private float maxForce;
  private float angle;

  public ParticleGenerator(int numParticles, float maxForce, Bounds bounds) {
    this.maxForce = maxForce;
    this.numParticles = numParticles;
    this.renderers = new ArrayList<>();

    this.particleSystem = ParticleSystem.boundedSystem(bounds);
    this.particleSystem.fillWithParticles(numParticles);
  }

  @Override
  public void setup() {}

  @Override
  public void draw(PGraphics graphics) {
    particleSystem.update();

    angle += 0.01;

    graphics.strokeWeight(1);
    graphics.rotateX(0.5f + angle);
    graphics.rotateY((float) Math.PI / 5 + angle);

    Bounds bounds = particleSystem.getBounds();
    graphics.box(bounds.getWidth(), bounds.getHeight(), bounds.getDepth());

    graphics.strokeWeight(1);
    for (ParticleRenderer r : renderers) {
      r.render(graphics, particleSystem.getParticles());
    }
  }

  public void addRenderer(ParticleRenderer renderer) {
    renderers.add(renderer);
  }

  public void addBehavior(ParticleBehavior behavior) {
    particleSystem.addBehavior(behavior);
  }
}
