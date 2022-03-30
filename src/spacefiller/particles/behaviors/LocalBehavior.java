package spacefiller.particles.behaviors;

import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;

import java.util.stream.Stream;

public abstract class LocalBehavior {
  private ParticleSystem particleSystem;
  private ParticleTag applyTo;

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }
  public void setParticleSystem(ParticleSystem particleSystem) {
    this.particleSystem = particleSystem;
  }
  public abstract void apply(Particle particle);

  public void setTagConstraint(ParticleTag tag) {
    this.applyTo = tag;
  }

  public ParticleTag getTag() {
    return applyTo;
  }
}
