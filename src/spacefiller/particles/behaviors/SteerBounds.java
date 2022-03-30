package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class SteerBounds extends LocalBehavior {
  private float threshold = 20;
  private float maxSpeed = 4;
  private float maxForce = 0.5f;

  public SteerBounds(float threshold, float maxSpeed, float maxForce) {
    this.threshold = threshold;
    this.maxSpeed = maxSpeed;
    this.maxForce = maxForce;
  }

  public SteerBounds() {
    this(20, 4, 0.5f);
  }

  public float getThreshold() {
    return threshold;
  }

  public void setThreshold(float threshold) {
    this.threshold = threshold;
  }

  public float getMaxSpeed() {
    return maxSpeed;
  }

  public void setMaxSpeed(float maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public float getMaxForce() {
    return maxForce;
  }

  public void setMaxForce(float maxForce) {
    this.maxForce = maxForce;
  }

  @Override
  public void apply(Particle particle) {
    Vector topLeft = getParticleSystem().getBounds().getTopBackLeft();
    Vector bottomRight = getParticleSystem().getBounds().getBottomFrontRight();

    Vector desired = null;

    if (particle.getPosition().x < topLeft.x + threshold) {
      desired = new Vector(maxSpeed, particle.getVelocity().y);
    }

    if (particle.getPosition().x > bottomRight.x - threshold) {
      desired = new Vector(-maxSpeed, particle.getVelocity().y);
    }

    if (particle.getPosition().y < topLeft.y + threshold) {
      desired = new Vector(particle.getVelocity().x, maxSpeed);
    }

    if (particle.getPosition().y > bottomRight.y - threshold) {
      desired = new Vector(particle.getVelocity().x, -maxSpeed);
    }

    if (desired != null) {
      Vector steer = Vector.sub(desired, particle.getVelocity());
      steer.limit(maxForce);
      particle.applyForce(steer);
    }
  }
}
