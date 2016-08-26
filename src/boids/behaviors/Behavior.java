package boids.behaviors;

import boids.Boid;
import boids.Flock;

import java.io.Serializable;
import java.util.List;


public abstract class Behavior implements Serializable {
	private transient Flock flock;

	public Flock getFlock() {
		return flock;
	}

	public void setFlock(Flock flock) {
		this.flock = flock;
	}

	public abstract void apply();
}
