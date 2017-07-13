package particles.behaviors;

import particles.Particle;

import java.util.List;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleFriction extends ParticleBehavior {
  private float friction;

  public ParticleFriction(float friction) {
    this.friction = friction;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p : particles) {
      p.applyFriction(friction);
    }
  }
}
