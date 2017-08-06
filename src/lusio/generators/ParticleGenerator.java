package lusio.generators;

import particles.Bounds;
import particles.Particle;
import particles.ParticleSystem;
import particles.behaviors.ParticleBehavior;
import particles.renderers.ParticleRenderer;
import processing.core.PGraphics;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator extends SceneGenerator {
  private ParticleSystem particleSystem;
  private List<ParticleRenderer> renderers;
  private Quaternion quaternion = new Quaternion();

  // TODO: why does this need to have a max force?

  public ParticleGenerator(int numParticles, Bounds bounds) {
    this(numParticles, bounds, 3);
  }

  public ParticleGenerator(int numParticles, Bounds bounds, int dimension) {
    this.renderers = new ArrayList<>();

    this.particleSystem = ParticleSystem.boundedSystem(bounds);
    this.particleSystem.fillWithParticles(numParticles, dimension);
  }

  @Override
  public void draw(PGraphics graphics) {
    particleSystem.update();

    float[] axis = quaternion.toAxisAngle();
    graphics.rotate(axis[0], axis[1], axis[3], axis[2]);

    graphics.strokeWeight(1);
    for (ParticleRenderer r : renderers) {
      r.render(graphics);
    }
  }

  public void addRenderer(ParticleRenderer renderer) {
    renderers.add(renderer);
    renderer.setParticles(particleSystem.getParticles());
    particleSystem.registerEventListener(renderer);
  }

  public void addBehavior(ParticleBehavior behavior) {
    particleSystem.addBehavior(behavior);
  }

  public void setRotation(Quaternion quaternion) {
    this.quaternion = quaternion;
  }

  public Bounds getBounds() {
    return this.particleSystem.getBounds();
  }

  public void setBounds(Bounds bounds) {
    this.particleSystem.setBounds(bounds);
  }

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }
}
