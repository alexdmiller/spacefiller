package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class RepelParticles extends AsymmetricParticleBehavior {
  private float repelThreshold;
  private float repelStrength;
  private boolean teams = false;

  public RepelParticles(float repelThreshold, float repelStrength) {
    this(repelThreshold, repelStrength, false);
  }

  public RepelParticles(float repelThreshold, float repelStrength, boolean global) {
    this.repelThreshold = repelThreshold;
    this.repelStrength = repelStrength;
    this.setGlobal(global);
  }

  @Override
  public void apply(Particle particle, Stream<Particle> neighbors) {
    neighbors.forEach(p2 -> {
      if (!teams || particle.getTeam() == p2.getTeam()) {
        Vector delta = Vector.sub(particle.getPosition(), p2.getPosition());
        float mag = (float) delta.magnitude();
        if (mag < repelThreshold) {
          float force = repelStrength / (mag * mag + 0.01f);
          delta.mult(force);
          particle.getVelocity().add(delta);
        }
      }
    });
  }

  public float getRepelThreshold() {
    return repelThreshold;
  }

  public void setRepelThreshold(float repelThreshold) {
    this.repelThreshold = repelThreshold;
  }

  public float getRepelStrength() {
    return repelStrength;
  }

  public void setRepelStrength(float repelStrength) {
    this.repelStrength = repelStrength;
  }

  public void setTeams(boolean teams) {
    this.teams = teams;
  }
}
