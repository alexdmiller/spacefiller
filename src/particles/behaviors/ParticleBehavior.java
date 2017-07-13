package particles.behaviors;

import particles.Particle;
import particles.ParticleSystem;
import processing.core.PGraphics;

import java.util.List;

public abstract class ParticleBehavior {
  private ParticleSystem particleSystem;

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }

  public void setParticleSystem(ParticleSystem particleSystem) {
    this.particleSystem = particleSystem;
  }

  public abstract void apply(List<Particle> particles);
}
