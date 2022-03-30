package spacefiller.particles.behaviors;

import spacefiller.particles.Particle;

public class FatalBounds extends LocalBehavior {
  @Override
  public void apply(Particle particle) {
    if (!getParticleSystem().getBounds().contains(particle.getPosition())) {
      particle.setRemoveFlag(true);
    }
  }
}
