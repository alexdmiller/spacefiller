package spacefiller.particles.behaviors;

import spacefiller.math.sdf.FloatField2;
import spacefiller.math.sdf.FloatField3;
import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlockParticles extends AsymmetricParticleBehavior {
  public enum TeamMode {
    ALL, DIFFERENT, SAME
  }

  private float separationWeight = 1;
  public float alignmentWeight = 1;
  public float cohesionWeight = 1;
  private float alignmentThreshold = 100;
  private float cohesionThreshold = 100;
  private TeamMode teamMode = TeamMode.ALL;
  private FloatField3 desiredSeparation;
  private FloatField2 separationField = FloatField2.ONE;
  private FloatField2 cohesionField = FloatField2.ONE;
  private FloatField2 alignmentField = FloatField2.ONE;
  private FloatField2 maxSpeed = FloatField2.ONE;
  private FloatField2 maxForce = FloatField2.ONE;

  public FlockParticles(
      float separationWeight,
      float alignmentWeight,
      float cohesionWeight,
      float desiredSeparation,
      float alignmentThreshold,
      float cohesionThreshold,
      float maxForce,
      float maxSpeed) {
    this.separationWeight = separationWeight;
    this.alignmentWeight = alignmentWeight;
    this.cohesionWeight = cohesionWeight;
    this.desiredSeparation = new FloatField3.Constant(desiredSeparation);
    this.alignmentThreshold = alignmentThreshold;
    this.cohesionThreshold = cohesionThreshold;
    this.maxForce = new FloatField2.Constant(maxForce);
    this.maxSpeed = new FloatField2.Constant(maxSpeed);
  }

  public FlockParticles() {
    this(2, 1, 1, 30, 100, 100, 0.1f, 4);
  }

  public spacefiller.particles.behaviors.FlockParticles setMaxSpeed(FloatField2 maxSpeed) {
    this.maxSpeed = maxSpeed;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setMaxSpeed(float maxSpeed) {
    this.maxSpeed = new FloatField2.Constant(maxSpeed);
    return this;
  }

  public FloatField3 getDesiredSeparation() {
    return desiredSeparation;
  }

  public spacefiller.particles.behaviors.FlockParticles setDesiredSeparation(FloatField3 desiredSeparation) {
    this.desiredSeparation = desiredSeparation;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setDesiredSeparation(float desiredSeparation) {
    this.desiredSeparation = new FloatField3.Constant(desiredSeparation);
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setCohesionThreshold(float cohesionThreshold) {
    this.cohesionThreshold = cohesionThreshold;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setCohesionWeight(float cohesionWeight) {
    this.cohesionWeight = cohesionWeight;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setAlignmentThreshold(float alignmentThreshold) {
    this.alignmentThreshold = alignmentThreshold;
    return this;
  }

  public void setAlignmentWeight(float alignmentWeight) {
    this.alignmentWeight = alignmentWeight;
  }

  public void setSeparationWeight(float separationWeight) {
    this.separationWeight = separationWeight;
  }

  public spacefiller.particles.behaviors.FlockParticles setSeparationField(FloatField2 separationField) {
    this.separationField = separationField;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setCohesionField(FloatField2 cohesionField) {
    this.cohesionField = cohesionField;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setAlignmentField(FloatField2 alignmentField) {
    this.alignmentField = alignmentField;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setMaxForce(FloatField2 maxForce) {
    this.maxForce = maxForce;
    return this;
  }

  public spacefiller.particles.behaviors.FlockParticles setMaxForce(float maxForce) {
    this.maxForce = new FloatField2.Constant(maxForce);
    return this;
  }

  @Override
  public void apply(Particle p, Stream<Particle> particles) {
    float localMaxSpeed = maxSpeed.get(p.getPosition().x, p.getPosition().y);
    float localMaxForce = maxForce.get(p.getPosition().x, p.getPosition().y);

    List<Particle> particleList = particles.collect(Collectors.toList());
    Vector sep = separate(p, particleList, localMaxSpeed, localMaxForce);
    Vector ali = align(p, particleList, localMaxSpeed, localMaxForce);
    Vector coh = cohesion(p, particleList, localMaxSpeed, localMaxForce);

    sep.mult(separationWeight * separationField.get(p.getPosition().x, p.getPosition().y));
    ali.mult(alignmentWeight * alignmentField.get(p.getPosition().x, p.getPosition().y));
    coh.mult(cohesionWeight * cohesionField.get(p.getPosition().x, p.getPosition().y));

    if (p.hasUserData("target")) {
      Vector target = (Vector) p.getUserData("target");
      p.applyForce(ParticleUtils.getSeekVector(p, target, localMaxSpeed, localMaxForce));
    }

    p.applyForce(sep);
    p.applyForce(ali);
    p.applyForce(coh);
  }

  private boolean shouldCompareParticles(Particle p1, Particle p2) {
    if (p1 == p2) {
      return false;
    }

    if (teamMode == TeamMode.ALL) {
      return true;
    }

    if (teamMode == TeamMode.DIFFERENT) {
      return p1.getTeam() != p2.getTeam();
    }

    if (teamMode == TeamMode.SAME) {
      return p1.getTeam() == p2.getTeam();
    }

    return true;
  }

  private Vector steer(Particle p, Vector target, float maxSpeed, float maxForce) {
    Vector desired = Vector.sub(target, p.getPosition());
    desired.normalize();
    desired.mult(maxSpeed);

    Vector steer = Vector.sub(desired, p.getVelocity());
    steer.limit(maxForce);
    return steer;
  }

  // Separation
  // Method checks for nearby boids and steers away
  Vector separate(Particle p, List<Particle> particles, float maxSpeed, float maxForce) {
    Vector steer = new Vector(0, 0, 0);

    int count = 0;
    // For every boid in the system, check if it's too close
    for (Particle other : particles) {
      //if (shouldCompareParticles(p, other)) {
        float d = p.getPosition().dist(other.getPosition());

        float separation = desiredSeparation.get(p.getPosition().x, p.getPosition().y, p.getTeam() == other.getTeam() ? 1 : 2) * (p.getTeam() == other.getTeam() ? 1 : 10);

        if (other != p && (d < separation)) {
          // Calculate vector pointing away from neighbor
          Vector diff = Vector.sub(p.getPosition(), other.getPosition());
          diff.normalize();
          diff.div(d);        // Weight by distance
          steer.add(diff);
          count++;            // Keep track of how many
        }
      //}
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.magnitude() > 0) {
      steer.normalize();
      steer.mult(maxSpeed);
      steer.sub(p.getVelocity());
      steer.limit(maxForce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  Vector align(Particle p, List<Particle> particles, float maxSpeed, float maxForce) {
    Vector sum = new Vector(0, 0);
    int count = 0;
    for (Particle other : particles) {
      if (shouldCompareParticles(p, other)) {
        float d = (float) p.getPosition().dist(other.getPosition());
        if ((d > 0) && (d < alignmentThreshold)) {
          sum.add(other.getVelocity());
          count++;
        }
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // First two lines of code below could be condensed with new Vector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      if (sum.magnitude() > 0) {
        sum.normalize();
      }

      sum.mult(maxSpeed);
      Vector steer = Vector.sub(sum, p.getVelocity());
      steer.limit(maxForce);
      return steer;
    }
    else {
      return new Vector(0, 0);
    }
  }

  // Cohesion
  // For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
  Vector cohesion(Particle p, List<Particle> particles, float maxSpeed, float maxForce) {
    Vector sum = new Vector(0, 0);   // Start with empty vector to accumulate all locations
    int count = 0;
    for (Particle other : particles) {
      if (shouldCompareParticles(p, other)) {
        float d = (float) p.getPosition().dist(other.getPosition());
        float cohesionThreshold2 = cohesionThreshold;
        if ((d > 0) && (d < cohesionThreshold2)) {
          sum.add(other.getPosition()); // Add position
          count++;
        }
      }
    }
    if (count > 0) {
      sum.div(count);
      return ParticleUtils.getSeekVector(p, sum, maxSpeed, maxForce);  // Steer towards the position
    }
    else {
      return new Vector(0, 0);
    }
  }

  public TeamMode getTeamMode() {
    return teamMode;
  }

  public spacefiller.particles.behaviors.FlockParticles setTeamMode(TeamMode teamMode) {
    this.teamMode = teamMode;
    return this;
  }

  public static final class Parameters {
    public float separationWeight = 1;
    public float alignmentWeight = 1;
    public float cohesionWeight = 1;
    public float alignmentThreshold = 100;
    public float cohesionThreshold = 100;
    public float desiredSeparation = 50;
    public float maxSpeed = 4f;
    public float maxForce = 1f;

    public Parameters(float separationWeight, float alignmentWeight, float cohesionWeight, float alignmentThreshold, float cohesionThreshold, float desiredSeparation, float maxSpeed, float maxForce) {
      this.separationWeight = separationWeight;
      this.alignmentWeight = alignmentWeight;
      this.cohesionWeight = cohesionWeight;
      this.alignmentThreshold = alignmentThreshold;
      this.cohesionThreshold = cohesionThreshold;
      this.desiredSeparation = desiredSeparation;
      this.maxSpeed = maxSpeed;
      this.maxForce = maxForce;
    }
  }

  public FlockParticles setParameters(Parameters parameters) {
    this.separationWeight = parameters.separationWeight;
    this.alignmentWeight = parameters.alignmentWeight;
    this.cohesionWeight = parameters.cohesionWeight;
    this.alignmentThreshold = parameters.alignmentThreshold;
    this.cohesionThreshold = parameters.cohesionThreshold;
    this.desiredSeparation = new FloatField3.Constant(parameters.desiredSeparation);
    this.maxSpeed = new FloatField2.Constant(parameters.maxSpeed);
    this.maxForce = new FloatField2.Constant(parameters.maxForce);
    return this;
  }
}