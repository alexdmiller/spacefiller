package spacefiller.particles.behaviors;

import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class ReflectiveBounds extends LocalBehavior {
  @Override
  public void apply(Particle particles) {
    getParticleSystem().getBounds().constrain(particles);
  }
}
