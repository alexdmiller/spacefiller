package lab;

import processing.core.PGraphics;
import sketches.Tubes;
import spacefiller.remote.Mod;
import toxi.geom.Vec3D;
import toxi.physics3d.VerletParticle3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Flock {
  @Mod
  public float separationWeight = 1;

  @Mod
  public float alignmentWeight = 1;

  @Mod
  public float cohesionWeight = 1;

  @Mod(min = 10, max = 50)
  public float desiredSeparation = 10;

  @Mod(min = 0, max = 300)
  public float alignmentThreshold = 100;

  @Mod(min = 0, max = 300)
  public float cohesionThreshold = 100;

  private float maxForce = 1;

  @Mod(min = 1f, max = 5)
  public float maxSpeed = 2;

  private PGraphics graphics;

  public Flock(
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

  public void setGraphics(PGraphics graphics) {
    this.graphics = graphics;
  }

  public float getMaxForce() {
    return maxForce;
  }

  public void setMaxSpeed(float maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public float getDesiredSeparation() {
    return desiredSeparation;
  }

  public void setDesiredSeparation(float desiredSeparation) {
    this.desiredSeparation = desiredSeparation;
  }

  public void setCohesionThreshold(float cohesionThreshold) {
    this.cohesionThreshold = cohesionThreshold;
  }

  public void setCohesionWeight(float cohesionWeight) {
    this.cohesionWeight = cohesionWeight;
  }

  public void setAlignmentThreshold(float alignmentThreshold) {
    this.alignmentThreshold = alignmentThreshold;
  }

  public void apply(List<Tubes.Worm> worms) {
    for (Tubes.Worm worm : worms) {
      List<Tubes.Worm> otherWorms = new ArrayList<>(worms);
      otherWorms.remove(worm);
      List<VerletParticle3D> others = new ArrayList<>();
      for (Tubes.Worm otherWorm : otherWorms) {
        others.addAll(Arrays.asList(otherWorm.getSegments()));
      }

      applyFlockingForces(worm.getHead(), others);
    }

//    for (VerletParticle3D p : particles) {
//      applyFlockingForces(p, particles);
//    }
  }

  // We accumulate a new acceleration each time based on three rules
  void applyFlockingForces(VerletParticle3D p, List<VerletParticle3D> particles) {
    Vec3D sep = separate(p, particles);   // Separation
    Vec3D ali = align(p, particles);      // Alignment
    Vec3D coh = cohesion(p, particles);   // Cohesion

    // Arbitrarily weight these forces
//    if (sep.magnitude() > 0) {
//      ali.scaleSelf(0);
//      coh.scaleSelf(0);
//    }
    sep.scaleSelf(separationWeight);
    ali.scaleSelf(alignmentWeight);
    coh.scaleSelf(cohesionWeight);
    // Add the force vectors to acceleration

    p.addForce(sep);
    p.addForce(ali);
    p.addForce(coh);

    coh = coh.scale(2);
    ali = ali.scale(10);

    if (graphics != null) {
      graphics.pushMatrix();
      graphics.translate(p.x, p.y, p.z);

      graphics.stroke(255, 0, 0);
      graphics.line(0, 0, 0, coh.x, coh.y, coh.z);

      graphics.stroke(0, 255, 0);
      graphics.line(0, 0, 0, ali.x, ali.y, ali.z);

      graphics.popMatrix();
    }
  }

  // Separation
  // Method checks for nearby boids and steers away
  Vec3D separate(VerletParticle3D p, List<VerletParticle3D> particles) {
    Vec3D steer = new Vec3D(0, 0, 0);

    int count = 0;
    // For every boid in the system, check if it's too close
    for (VerletParticle3D other : particles) {
      float d = p.distanceTo(other);
      if (other != p && (d < desiredSeparation)) {
        // Calculate vector pointing away from neighbor
        Vec3D diff = p.sub(other);
        diff.normalize();
        diff.scaleSelf(1 / (float) d);        // Weight by distance
        steer.addSelf(diff);
        count++;            // Keep track of how many
      }
    }
    // Average -- divide by how many
    if (count > 0) {
      steer.scaleSelf(1 / (float) count);
    }

    // As long as the vector is greater than 0
    if (steer.magnitude() > 0) {

      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // steer.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      steer.normalize();
      steer.scaleSelf(maxSpeed);
      steer.subSelf(p.getVelocity());
      steer.limit(maxForce);
    }
    return steer;
  }

  // Alignment
  // For every nearby boid in the system, calculate the average velocity
  Vec3D align(VerletParticle3D p, List<VerletParticle3D> particles) {
    Vec3D sum = new Vec3D();
    int count = 0;
    for (VerletParticle3D other : particles) {
      float d = p.distanceTo(other);
      if ((d > 0) && (d < alignmentThreshold)) {
        sum.add(other.getVelocity());
        count++;
      }
    }
    if (count > 0) {
      sum.scaleSelf(1 / count);
      // First two lines of code below could be condensed with new PVector setMag() method
      // Not using this method until Processing.js catches up
      // sum.setMag(maxSpeed);

      // Implement Reynolds: Steering = Desired - Velocity
      sum.normalize();
      sum.scaleSelf(maxSpeed);
      Vec3D steer = sum.sub(p.getVelocity());
      steer.limit(maxForce);
      return steer;
    }
    else {
      return new Vec3D();
    }
  }

  // Cohesion
  // For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
  Vec3D cohesion(VerletParticle3D p, List<VerletParticle3D> particles) {
    Vec3D sum = new Vec3D();   // Start with empty vector to accumulate all locations
    int count = 0;
    for (VerletParticle3D other : particles) {
      float d = p.distanceTo(other);
      if ((d > 0) && (d < cohesionThreshold)) {
        sum.addSelf(other); // Add position
        count++;
      }
    }
    if (count > 0) {
      sum.scaleSelf(1 / count);
      return seek(p, sum, maxSpeed, maxForce);  // Steer towards the position
    }
    else {
      return new Vec3D();
    }
  }

  public static Vec3D seek(VerletParticle3D p, Vec3D target, float maxSpeed, float maxForce) {
    Vec3D desired = target.sub(p);  // A vector pointing from the position to the target
    desired.normalize();
    desired.scaleSelf(maxSpeed);
    Vec3D steer = desired.sub(p.getVelocity());
    steer.limit(maxForce);  // Limit to maximum steering force
    return steer;
  }
}