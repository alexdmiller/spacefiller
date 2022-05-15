package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class AttractParticles extends AsymmetricParticleBehavior {
  private float attractThreshold;
  private float attractStrength;

  public AttractParticles(float attractThreshold, float attractStrength) {
    this.attractThreshold = attractThreshold;
    this.attractStrength = attractStrength;
  }

  @Override
  public void apply(Particle particle, Stream<Particle> neighbors) {
    neighbors.forEach(p2 -> {
      Vector delta = Vector.sub(particle.getPosition(), p2.getPosition());
      float dist = (float) delta.magnitude();
      if (dist < attractThreshold) {
        delta.setMag(attractStrength);
        p2.applyForce(delta);
        delta.mult(-1);
        particle.applyForce(delta);
      }
    });
  }
}
