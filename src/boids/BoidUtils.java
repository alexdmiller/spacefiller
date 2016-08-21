package boids;

import processing.core.PVector;

public class BoidUtils {
	// A method that calculates and applies a steering force towards a target
	// STEER = DESIRED MINUS VELOCITY
	public static PVector seek(Boid b, PVector target, float maxSpeed, float maxForce) {
		PVector desired = PVector.sub(target, b.getPosition());  // A vector pointing from the position to the target
		// Scale to maximum speed
		desired.normalize();
		desired.mult(maxSpeed);

		// Above two lines of code below could be condensed with new PVector setMag() method
		// Not using this method until Processing.js catches up
		// desired.setMag(maxSpeed);

		// Steering = Desired minus Velocity
		PVector steer = PVector.sub(desired, b.getVelocity());
		steer.limit(maxForce);  // Limit to maximum steering force
		return steer;
	}
}
