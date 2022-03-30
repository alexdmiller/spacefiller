package spacefiller.particles;

import spacefiller.math.Vector;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleUtils {
  public static Vector getSeekVector(Particle p, Vector target, float maxSpeed, float maxForce) {
    Vector desired = Vector.sub(target, p.getPosition());  // A vector pointing from the position to the target
    desired.normalize();
    desired.mult(maxSpeed);
    Vector steer = Vector.sub(desired, p.getVelocity());
    steer.limit(maxForce);  // Limit to maximum steering force
    return steer;
  }

  public static Vector getAvoidanceVector(Particle p, Vector target, float maxSpeed, float maxForce) {
    return getAvoidanceVector(p, target, maxSpeed, maxForce, -1);
  }

  public static Vector getAvoidanceVector(Particle p, Vector target, float maxSpeed, float maxForce, float threshold) {
    Vector desired = Vector.sub(target, p.getPosition());  // A vector pointing from the position to the target

    if (threshold >= 0 && desired.magnitude() > threshold) {
      return new Vector();
    }

    desired.normalize();
    desired.mult(-maxSpeed);
    Vector steer = Vector.sub(desired, p.getVelocity());
    steer.limit(maxForce);  // Limit to maximum steering force
    return steer;
  }

}
