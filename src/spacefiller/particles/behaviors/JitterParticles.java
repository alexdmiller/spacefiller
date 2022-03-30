package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class JitterParticles extends LocalBehavior {
  private float jitterStrength = 1;

  public JitterParticles(float jitterStrength) {
    this.jitterStrength = jitterStrength;
  }

  @Override
  public void apply(Particle particle) {
    particle.applyForce(Vector.random3D().setMag(jitterStrength));
  }
}
