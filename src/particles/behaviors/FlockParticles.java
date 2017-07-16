package particles.behaviors;

import boids.Flock;
import particles.Particle;
import particles.ParticleUtils;
import processing.core.PVector;

import java.util.List;

public class FlockParticles extends ParticleBehavior {
  private float separationWeight = 1;
  private float alignmentWeight = 1;
  private float cohesionWeight = 1;

  private float desiredSeparation = 50;
  private float alignmentThreshold = 100;
  private float cohesionThreshold = 100;

  private float maxForce = 1;
  private float maxSpeed = 10;

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
    this.desiredSeparation = desiredSeparation;
    this.alignmentThreshold = alignmentThreshold;
    this.cohesionThreshold = cohesionThreshold;
    this.maxForce = maxForce;
    this.maxSpeed = maxSpeed;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p : particles) {
      applyFlockingForces(p, particles);
    }
  }

  // We accumulate a new acceleration each time based on three rules
  void applyFlockingForces(Particle p, List<Particle> particles) {
    PVector sep = separate(p, particles);   // Separation
    PVector ali = align(p, particles);      // Alignment
    PVector coh = cohesion(p, particles);   // Cohesion
    // Arbitrarily weight these forces
    if (sep.mag() > 0) {
      ali.mult(0);
      coh.mult(0);
    }
    sep.mult(separationWeight);
    ali.mult(alignmentWeight);
    coh.mult(cohesionWeight);
    // Add the force vectors to acceleration
    p.applyForce(sep);
    p.applyForce(ali);
    p.applyForce(coh);
  }

  // Separation
  // Method checks for nearby boids and steers away
  PVector separate(Particle p, List<Particle> particles) {
    PVector steer = new PVector(0, 0, 0);

    int count = 0;
    // For every boid in the system, check if it's too close
    for (Particle other : particles) {
      float d = PVector.dist(p.position, other.position);
      float desiredSeparation2 = desiredSeparation;
      if (other != p && (d < desiredSeparation2)) {
        // Calculate vector pointing away from neighbor
        PVector diff = PVector.sub(p.position, other.position);
        diff.normalize();
        diff.div(d);        // Weight by distance
        steer.add(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.div((float)count);
    }

    // As long as the vector is greater than 0
    if (steer.mag() > 0) {
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // steer.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.mult(maxSpeed);
      steer.sub(p.velocity);
      steer.limit(maxForce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  PVector align(Particle p, List<Particle> particles) {
    PVector sum = new PVector(0, 0);
    int count = 0;
    for (Particle other : particles) {
      float d = PVector.dist(p.position, other.position);
      if ((d > 0) && (d < alignmentThreshold)) {
        sum.add(other.velocity);
        count++;
      }
    }
    if (count > 0) {
      sum.div((float)count);
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.mult(maxSpeed);
      PVector steer = PVector.sub(sum, p.velocity);
      steer.limit(maxForce);
      return steer;
    }
    else {
      return new PVector(0, 0);
    }
  }

  // Cohesion
  // For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
  PVector cohesion(Particle p, List<Particle> particles) {
    PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
    int count = 0;
    for (Particle other : particles) {
      float d = PVector.dist(p.position, other.position);
      if ((d > 0) && (d < cohesionThreshold)) {
        sum.add(other.position); // Add position
        count++;
      }
    }
    if (count > 0) {
      sum.div(count);
      return ParticleUtils.seek(p, sum, maxSpeed, maxForce);  // Steer towards the position
    }
    else {
      return new PVector(0, 0);
    }
  }
}