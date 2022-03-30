package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

import java.util.ArrayList;
import java.util.List;

public class RepelFixedPoints extends LocalBehavior {
  public float repelThreshold;
  public float repelStrength;

  private List<Vector> fixedPoints;

  public RepelFixedPoints(float repelThreshold, float repelStrength) {
    this.repelThreshold = repelThreshold;
    this.repelStrength = repelStrength;
    this.fixedPoints = new ArrayList<>();
  }

  public void setRepelThreshold(float repelThreshold) {
    this.repelThreshold = repelThreshold;
  }

  public void setRepelStrength(float repelStrength) {
    this.repelStrength = repelStrength;
  }

  public void addFixedPoint(Vector p) {
    fixedPoints.add(p);
  }

  public void clearPoints() {
    fixedPoints.clear();
  }

  public List<Vector> getFixedPoints() {
    return fixedPoints;
  }

  @Override
  public void apply(Particle particle) {
    for (Vector fixed : fixedPoints) {
      Vector delta = Vector.sub(particle.getPosition(), fixed);
      float dist = (float) delta.magnitude();
      if (dist < repelThreshold) {
        delta.setMag(repelStrength / dist);
        particle.applyForce(delta);
      }
    }
  }
}
