package common;

import processing.core.PVector;

import java.awt.*;

/**
 * Created by miller on 9/28/16.
 */
public class Particle {
	public PVector position;
	public PVector velocity;

	public Color color;

	public PVector forces;

	public Particle(float x, float y, float z, Color color) {
		this.position = new PVector(x, y, z);
		this.velocity = new PVector();
		this.forces = new PVector();
		this.color = color;
	}

	public Particle() {
		this.position = new PVector();
		this.velocity = new PVector();
		this.forces = new PVector();
	}

	public Particle(float x, float y, float z) {
		this.position = new PVector(x, y, z);
		this.velocity = new PVector();
		this.forces = new PVector();
	}

	public Particle(float x, float y) {
		this.position = new PVector(x, y);
		this.velocity = new PVector();
		this.forces = new PVector();
	}

	public Particle(PVector p) {
		this.position = p;
		this.velocity = new PVector();
		this.forces = new PVector();
	}

	public void update() {
		position.add(velocity);
	}

	public void applyFriction(float friction) {
		velocity.mult(friction);
	}

	public void flushForces(float limit) {
		forces.limit(limit);
		velocity.add(forces);
		forces.setMag(0);
	}

	public void applyForce(PVector force) {
		forces.add(force);
	}

	public void setRandomVelocity(float min, float max) {
		this.velocity = PVector.random3D();
		this.velocity.setMag((float) Math.random() * (max - min) + min);
	}
}
