package particles.behaviors;

import particles.Bounds;
import particles.Particle;

import java.util.List;

public class ReflectiveBounds extends ParticleBehavior {
  @Override
  public void apply(List<Particle> particles) {
    for (Particle p : particles) {
      getParticleSystem().getBounds().constrain(p);
    }
  }
}
