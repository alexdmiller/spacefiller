package particles;

import particles.behaviors.BoundParticles;
import particles.behaviors.ParticleBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleSystem {
  public static ParticleSystem boundedSystem(Bounds bounds) {
    ParticleSystem system = new ParticleSystem(bounds);
    system.addBehavior(new BoundParticles());
    return system;
  }

  private Bounds bounds;
  private List<Particle> particles;
  private List<ParticleBehavior> behaviors;
  private float maxForce = 10;

  public ParticleSystem(Bounds bounds) {
    this.bounds = bounds;
    this.particles = new ArrayList<>();
    this.behaviors = new ArrayList<>();
  }

  public void fillWithParticles(int numParticles) {
    for (int i = 0; i < numParticles; i++) {
      Particle p = new Particle(bounds.getRandomPointInside());
      p.setRandomVelocity(1, 2);
      particles.add(p);
    }
  }

  public void update() {
    for (ParticleBehavior behavior : behaviors) {
      behavior.apply(particles);
    }

    for (Particle p : particles) {
      p.flushForces(maxForce);
      p.update();
    }
  }

  public List<Particle> getParticles() {
    return particles;
  }

  public void addBehavior(ParticleBehavior behavior) {
    behavior.setParticleSystem(this);
    this.behaviors.add(behavior);
  }

  public Bounds getBounds() {
    return bounds;
  }
}
