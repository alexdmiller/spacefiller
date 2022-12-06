package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class SoftBounds extends LocalBehavior {
  private float threshold;
  private float slope;
  private float maxForce;

  public SoftBounds() {
    this(5, 2, 2);
  }

  public SoftBounds(float threshold, float slope, float maxForce) {
    this.threshold = threshold;
    this.slope = slope;
    this.maxForce = maxForce;
  }

  @Override
  public void apply(Particle particle) {
    Vector topBackLeft = getParticleSystem().getBounds().getTopBackLeft();
    Vector bottomFrontRight = getParticleSystem().getBounds().getBottomFrontRight();

    applySoftBoundary(particle, particle.getPosition().x - topBackLeft.x, new Vector(1, 0));
    applySoftBoundary(particle, bottomFrontRight.x  - particle.getPosition().x, new Vector(-1, 0));
    applySoftBoundary(particle, particle.getPosition().y  - topBackLeft.y, new Vector(0, 1));
    applySoftBoundary(particle, bottomFrontRight.y  - particle.getPosition().y, new Vector(0, -1));
  }

  private void applySoftBoundary(Particle p, float dist, Vector direction) {
    float force = forceFunction(dist);
    if (force > 0) {
      p.applyForce(direction.mult(force));
    }
  }

  public float forceFunction(float dist) {
    if (dist < 0) {
      return maxForce;
    } else if (dist < threshold) {
      return (float) (slope / (dist + 1));
    } else {
      return 0;
    }
  }
}
