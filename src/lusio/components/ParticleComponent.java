package lusio.components;

import particles.Bounds;
import particles.ParticleSystem;
import particles.behaviors.ParticleBehavior;
import particles.renderers.ParticleRenderer;
import processing.core.PGraphics;
import scene.SceneComponent;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class ParticleComponent extends SceneComponent {
  private ParticleSystem particleSystem;
  private List<ParticleRenderer> renderers;
  private Quaternion quaternion = new Quaternion();

  // TODO: why does this need to have a max force?

  public ParticleComponent(int numParticles, Bounds bounds) {
    this(numParticles, bounds, 3);
  }

  public ParticleComponent(int numParticles, Bounds bounds, int dimension) {
    this.renderers = new ArrayList<>();

    this.particleSystem = ParticleSystem.boundedSystem(bounds, numParticles);
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
