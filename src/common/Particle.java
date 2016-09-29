package common;

import processing.core.PVector;

/**
 * Created by miller on 9/28/16.
 */
public class Particle {
	public PVector position;
	public PVector velocity;

	public PVector forces;

	public Particle(float x, float y) {
		position = new PVector(x, y);
		velocity = new PVector();
		forces = new PVector();
	}

	public void update() {
		position.add(velocity);
	}

	public void applyFriction(float friction) {
		velocity.mult(friction);
	}

	public void flushForces() {
		velocity.add(forces);
		forces.setMag(0);
	}

	public void applyForce(PVector force) {
		forces.add(force);
	}
}
