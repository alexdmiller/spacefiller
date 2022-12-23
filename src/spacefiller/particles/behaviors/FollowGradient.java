package spacefiller.particles.behaviors;

import spacefiller.math.sdf.FloatField2;
import spacefiller.math.Vector;
import spacefiller.particles.Particle;

import static spacefiller.math.sdf.FloatField2.gradient;

public class FollowGradient extends LocalBehavior {
  private FloatField2 field;
  private float steepness;
  private boolean normalize;

  public FollowGradient(FloatField2 field, float steepness, boolean normalize) {
    this.field = field;
    this.steepness = steepness;
    this.normalize = normalize;
  }

  @Override public void apply(Particle particle) {
    Vector gradient = gradient(field, particle.getPosition());

    int numSamples = 5;
    if (particle.getRadius() > 1) {
      for (int i = 0; i < numSamples; i++) {
        float angle = (float) (((float) i / numSamples) * 2 * Math.PI);
        Vector sample = particle.getPosition().copy();
        sample.add(
            (float) (Math.cos(angle) * particle.getRadius()),
            (float) (Math.sin(angle) * particle.getRadius()));

        gradient.add(gradient(field, sample));
      }
    }
    particle.applyForce(
        normalize ? gradient.copy().normalize().mult(steepness) : gradient.copy().mult(steepness));
  }

  public FloatField2 getField() {
    return field;
  }
}
