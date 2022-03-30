package spacefiller.particles;

import spacefiller.particles.Particle;

/**
 * Created by miller on 7/31/17.
 */
public interface ParticleEventListener {
  void particleAdded(Particle particle);
  void particleRemoved(Particle particle);
}
