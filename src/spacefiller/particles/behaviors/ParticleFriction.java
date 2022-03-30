package spacefiller.particles.behaviors;

import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class ParticleFriction extends LocalBehavior {
  private float friction;

  public ParticleFriction(float friction) {
    this.friction = friction;
  }

  @Override
  public void apply(Particle p) {
    p.applyFriction(friction);
  }
}
