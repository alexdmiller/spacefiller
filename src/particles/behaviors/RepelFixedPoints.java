package particles.behaviors;

import spacefiller.remote.Mod;
import particles.Particle;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import particles.Particle;
import processing.core.PVector;

import java.util.List;

public class RepelFixedPoints extends ParticleBehavior {
  @Mod(min = 0, max = 200)
  public float repelThreshold;

  @Mod(min = -0.1f, max = 0.1f)
  public float repelStrength;

  private List<PVector> fixedPoints;

  public RepelFixedPoints (float repelThreshold, float repelStrength) {
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

  public void addFixedPoint(PVector p) {
    fixedPoints.add(p);
  }

  public List<PVector> getFixedPoints() {
    return fixedPoints;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p1 : particles) {
      for (PVector fixed : fixedPoints) {
        PVector delta = PVector.sub(p1.position, fixed);
        float dist = delta.mag();
        if (dist < repelThreshold) {
          delta.setMag(dist * repelStrength);
          p1.applyForce(delta);
        }
      }
    }
  }
}
