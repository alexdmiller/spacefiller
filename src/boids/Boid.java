package boids;

import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

public class Boid {
	private PVector position;
	private PVector velocity;
	private PVector acceleration;

	private Map<String, Object> userData;

	public Boid(float x, float y) {
		acceleration = new PVector(0, 0);
		velocity = new PVector(0, 0);
		position = new PVector(x, y);
		userData = new HashMap<>();
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

	public void applyForce(PVector force) {
		// We could add mass here if we want A = F / M
		acceleration.add(force);
	}

	public void setUserData(String key, Object o) {
		userData.put(key, o);
	}

	public boolean hasUserData(String key) {
		return userData.containsKey(key);
	}

	public Object getUserData(String key) {
		return userData.get(key);
	}

	void update() {
		velocity.add(acceleration);
		position.add(velocity);

		// Reset accelertion to 0 each cycle
		acceleration.mult(0);
	}
}