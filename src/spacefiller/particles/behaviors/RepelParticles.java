package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.math.sdf.FloatField2;
import spacefiller.particles.Particle;

import java.util.stream.Stream;

public class RepelParticles extends AsymmetricParticleBehavior {
//  private float repelThreshold;
  private float repelStrength;
  private boolean teams = false;
  private boolean respectRadius = false;
  private FloatField2 repelThreshold;

  public RepelParticles(float repelThreshold, float repelStrength) {
    this(repelThreshold, repelStrength, false);
  }

  public RepelParticles(float repelThreshold, float repelStrength, boolean respectRadius) {
    this.repelThreshold = (x, y) -> repelThreshold;
    this.repelStrength = repelStrength;
    this.respectRadius = respectRadius;
  }

  public RepelParticles(FloatField2 repelThreshold, float repelStrength, boolean respectRadius) {
    this.repelThreshold = repelThreshold;
    this.repelStrength = repelStrength;
    this.respectRadius = respectRadius;
  }

  @Override
  public void apply(Particle particle, Stream<Particle> neighbors) {
    neighbors.forEach(p2 -> {
      if (!teams || particle.getTeam() == p2.getTeam()) {
        Vector delta = Vector.sub(particle.getPosition(), p2.getPosition());
        float mag = delta.magnitude();


        float localRepelThreshold = repelThreshold.get(
            particle.getPosition().x, particle.getPosition().y);
        if (respectRadius) {
          localRepelThreshold += particle.getRadius() + p2.getRadius();
        }

        if (mag < localRepelThreshold) {
          float force = repelStrength / (mag * mag + 0.01f);
          delta.mult(force);
          particle.getVelocity().add(delta);
        }
      }
    });
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
