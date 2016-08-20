package boids;

import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Boid {
	private PVector position;
	private PVector velocity;
	private PVector acceleration;
	private float r;
	private float maxforce;    // Maximum steering force
	private float maxspeed;    // Maximum speed

	public Boid(float x, float y) {
		acceleration = new PVector(0, 0);

		// This is a new PVector method not yet implemented in JS
		// velocity = PVector.random2D();

		// Leaving the code temporarily this way so that this example runs in JS
		float angle = (float) (Math.random() * Math.PI * 2);
		velocity = new PVector((float) Math.cos(angle), (float) Math.sin(angle));

		position = new PVector(x, y);
		r = 2.0f;
		maxspeed = 2f;
		maxforce = 0.03f;
	}

	public PVector getPosition() {
		return position;
	}

	public void setPosition(PVector position) {
		this.position.set(position);
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	public PVector getVelocity() {
		return velocity;
	}

	public void setVelocity(PVector velocity) {
		this.velocity.set(velocity);
	}

	public void setVelocity(float x, float y) {
		this.velocity.set(x, y);
	}

	public PVector getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(PVector acceleration) {
		this.acceleration.set(acceleration);
	}

	public void setAcceleration(float x, float y) {
		this.acceleration.set(x, y);
	}

	public void run(List<Boid> boids) {
		flock(boids);
		update();
	}

	public void applyForce(PVector force) {
		// We could add mass here if we want A = F / M
		acceleration.add(force);
	}


	// We accumulate a new acceleration each time based on three rules
	void flock(List<Boid> boids) {
		PVector sep = separate(boids);   // Separation
		PVector ali = align(boids);      // Alignment
		PVector coh = cohesion(boids);   // Cohesion
		// Arbitrarily weight these forces
		sep.mult(1.5f);
		ali.mult(1.0f);
		coh.mult(1.0f);
		// Add the force vectors to acceleration
		applyForce(sep);
		applyForce(ali);
		applyForce(coh);
	}

	// Method to update position
	void update() {
		// Update velocity
		velocity.add(acceleration);
		// Limit speed
		// velocity.limit(maxspeed);
		position.add(velocity);
		// Reset accelertion to 0 each cycle
		acceleration.mult(0);
	}

	// A method that calculates and applies a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	PVector seek(PVector target) {
		PVector desired = PVector.sub(target, position);  // A vector pointing from the position to the target
		// Scale to maximum speed
		desired.normalize();
		desired.mult(maxspeed);

		// Above two lines of code below could be condensed with new PVector setMag() method
		// Not using this method until Processing.js catches up
		// desired.setMag(maxspeed);

		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, velocity);
		steer.limit(maxforce);  // Limit to maximum steering force
		return steer;
	}

	// Separation
	// Method checks for nearby boids and steers away
	PVector separate (List<Boid> boids) {
		float desiredseparation = 25.0f;
		PVector steer = new PVector(0, 0, 0);
		int count = 0;
		// For every boid in the system, check if it's too close
		for (Boid other : boids) {
			float d = PVector.dist(position, other.position);
			// If the distance is greater than 0 and less than an arbitrary amount (0 when you are yourself)
			if ((d > 0) && (d < desiredseparation)) {
				// Calculate vector pointing away from neighbor
				PVector diff = PVector.sub(position, other.position);
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
			// steer.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			steer.normalize();
			steer.mult(maxspeed);
			steer.sub(velocity);
			steer.limit(maxforce);
		}
		return steer;
	}

	// Alignment
	// For every nearby boid in the system, calculate the average velocity
	PVector align (List<Boid> boids) {
		float neighbordist = 50;
		PVector sum = new PVector(0, 0);
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(position, other.position);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.velocity);
				count++;
			}
		}
		if (count > 0) {
			sum.div((float)count);
			// First two lines of code below could be condensed with new PVector setMag() method
			// Not using this method until Processing.js catches up
			// sum.setMag(maxspeed);

			// Implement Reynolds: Steering = Desired - Velocity
			sum.normalize();
			sum.mult(maxspeed);
			PVector steer = PVector.sub(sum, velocity);
			steer.limit(maxforce);
			return steer;
		}
		else {
			return new PVector(0, 0);
		}
	}

	// Cohesion
	// For the average position (i.e. center) of all nearby boids, calculate steering vector towards that position
	PVector cohesion (List<Boid> boids) {
		float neighbordist = 50;
		PVector sum = new PVector(0, 0);   // Start with empty vector to accumulate all locations
		int count = 0;
		for (Boid other : boids) {
			float d = PVector.dist(position, other.position);
			if ((d > 0) && (d < neighbordist)) {
				sum.add(other.position); // Add position
				count++;
			}
		}
		if (count > 0) {
			sum.div(count);
			return seek(sum);  // Steer towards the position
		}
		else {
			return new PVector(0, 0);
		}
	}
}