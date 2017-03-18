package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;
import modulation.Mod;

import java.util.List;

public class FlockBehavior extends Behavior {
	@Mod(min = 0, max = 10, defaultValue = 1)
	public float separationWeight = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float alignmentWeight = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float cohesionWeight =  1;

	@Mod(min = 0, max = 1, defaultValue = 0.5f)
	public float maxForce = 0.5f;

	@Mod(min = 0, max = 200, defaultValue = 20)
	public float desiredSeparation = 20;

	@Mod(min = 0, max = 200, defaultValue = 50)
	public float alignmentThreshold = 50;

	@Mod(min = 0, max = 200, defaultValue = 60)
	public float cohesionThreshold = 60;

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();
		for (Boid b : boids) {
			applyFlockingForces(b, boids);
		}
	}

	// We accumulate a new acceleration each time based on three rules
	void applyFlockingForces(Boid b, List<Boid> boids) {
		PVector sep = separate(b, boids);   // Separation
		PVector ali = align(b, boids);      // Alignment
		PVector coh = cohesion(b, boids);   // Cohesion
		// Arbitrarily weight these forces
		if (sep.mag() > 0) {
			ali.mult(0);
			coh.mult(0);
		}
		sep.mult(separationWeight);
		ali.mult(alignmentWeight);
		coh.mult(cohesionWeight);
		// Add the force vectors to acceleration
		b.applyForce(sep);
		b.applyForce(ali);
		b.applyForce(coh);
	}

	// Separation
	// Method checks for nearby boids and steers away
	PVector separate(Boid b, List<Boid> boids) {
		PVector steer = new PVector(0, 0, 0);

		int count = 0;
		// For every boid in the system, check if it's too close
		for (Boid other : boids) {
			float d = PVector.dist(b.getPosition(), other.getPosition());
			float desiredSeparation2 = desiredSeparation * (b.getTeam() == 0 && other.getTeam() == 0 ? 1 : 3);
			if (other != b && (d < desiredSeparation2)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(b.getPosition(), other.getPosition());
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
			steer.mult(getFlock().getMaxSpeed(b.getPosition().x, b.getPosition().y));
			steer.sub(b.getVelocity());
			steer.limit(maxForce);
		}
		return steer;
	}

	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	PVector align(Boid b, List<Boid> boids) {
		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(b.getPosition(), other.getPosition());
			if ((d > 0) && (d < alignmentThreshold)) {
				sum.add(other.getVelocity());
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
			sum.mult(getFlock().getMaxSpeed(b.getPosition().x, b.getPosition().y));
			PVector steer = PVector.sub(sum, b.getVelocity());
			steer.limit(maxForce);
			return steer;
		}
		else {
			return new PVector(0, 0);
		}
	}

	// Cohesion
	// For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
	PVector cohesion(Boid b, List<Boid> boids) {
		PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(b.getPosition(), other.getPosition());
			if ((d > 0) && (d < cohesionThreshold)) {
				sum.add(other.getPosition()); // Add position
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return BoidUtils.seek(b, sum, getFlock().getMaxSpeed(b.getPosition().x, b.getPosition().y), maxForce);  // Steer towards the position
		}
		else {
			return new PVector(0, 0);
		}
	}
}
