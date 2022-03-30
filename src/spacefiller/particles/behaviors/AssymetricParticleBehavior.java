package spacefiller.particles.behaviors;

import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;

import java.util.stream.Stream;

public abstract class AssymetricParticleBehavior {
  private ParticleSystem particleSystem;
  private ParticleTag applyTo;
  private ParticleTag filterNeighborsTo;
  private boolean global = false;

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }
  public void setParticleSystem(ParticleSystem particleSystem) {
    this.particleSystem = particleSystem;
  }
  public abstract void apply(Particle particle, Stream<Particle> neighbors);

  public void setTagConstraint(ParticleTag tag) {
    this.applyTo = tag;
  }

  public ParticleTag getTag() {
    return applyTo;
  }

  public void setNeighborFilter(ParticleTag tag) {
    this.filterNeighborsTo = tag;
  }

  public ParticleTag getNeighborFilter() {
    return filterNeighborsTo;
  }

  public void setGlobal(boolean value) {
    this.global = global;
  }

  public boolean isGlobal() {
    return global;
  }
}
