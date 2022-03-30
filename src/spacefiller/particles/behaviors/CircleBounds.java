package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleTag;

public class CircleBounds extends LocalBehavior {
  private float radius;
  private float slope;
  private float maxForce;

  public CircleBounds() {
    this(100, 2, 2);
  }

  public CircleBounds(float radius, float slope, float maxForce) {
    this.radius = radius;
    this.slope = slope;
    this.maxForce = maxForce;
  }

  @Override
  public void apply(Particle particle) {
    Vector center = getParticleSystem().getBounds().getCenter();

    Vector sub = center.sub(particle.getPosition());
    float dist = sub.magnitude();
    if (dist > radius) {
      particle.applyForce(sub.mult(0.01f));
    }
  }

  public float getRadius() {
    return radius;
  }

  public void setRadius(float radius) {
    this.radius = radius;
  }

  //  private void applySoftBoundary(Particle p, float dist, Vector direction) {
//    float force = forceFunction(dist);
//    if (force > 0) {
//      p.applyForce(direction.mult(force));
//    }
//  }

//  public float forceFunction(float dist) {
//    if (dist < 0) {
//      return maxForce;
//    } else if (dist < ) {
//      return (float) (slope / (dist + 1));
//    } else {
//      return 0;
//    }
//  }
}
