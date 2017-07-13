package particles.behaviors;

import particles.Particle;
import processing.core.PVector;

import java.util.List;

public class RepelParticles extends ParticleBehavior {
  private float repelThreshold;
  private float repelStrength;

  public RepelParticles(float repelThreshold, float repelStrength) {
    this.repelThreshold = repelThreshold;
    this.repelStrength = repelStrength;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p1 : particles) {
      for (Particle p2 : particles) {
        PVector delta = PVector.sub(p1.position, p2.position);
        float dist = delta.mag();
        if (dist < repelThreshold) {
          delta.setMag(repelStrength);
          p1.applyForce(delta);
          delta.mult(-1);
          p2.applyForce(delta);
        }
      }
    }
  }
}
