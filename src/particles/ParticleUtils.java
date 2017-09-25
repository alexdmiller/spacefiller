package particles;

import processing.core.PVector;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleUtils {
  public static PVector seek(Particle p, PVector target, float maxSpeed, float maxForce) {
    PVector desired = PVector.sub(target, p.position);  // A vector pointing from the position to the target
    desired.normalize();
    desired.mult(maxSpeed);
    PVector steer = PVector.sub(desired, p.velocity);
    steer.limit(maxForce);  // Limit to maximum steering force
    return steer;
  }

}
