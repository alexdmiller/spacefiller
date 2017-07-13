package particles.behaviors;

import particles.Particle;
import processing.core.PVector;

import java.util.List;

public class AttractParticles extends ParticleBehavior {
  private float attractThreshold;
  private float attractStrength;

  public AttractParticles(float attractThreshold, float attractStrength) {
    this.attractThreshold = attractThreshold;
    this.attractStrength = attractStrength;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p1 : particles) {
      for (Particle p2 : particles) {
        PVector delta = PVector.sub(p1.position, p2.position);
        float dist = delta.mag();
        if (dist < attractThreshold) {
          delta.setMag(attractStrength);
          p2.applyForce(delta);
          delta.mult(-1);
          p1.applyForce(delta);
        }
      }
    }
  }
}
