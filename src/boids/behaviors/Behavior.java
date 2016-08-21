package boids.behaviors;

import boids.Boid;
import boids.Flock;

import java.util.List;

/**
 * Created by miller on 8/20/16.
 */
public abstract class Behavior {
	private Flock flock;

	public Flock getFlock() {
		return flock;
	}

	public void setFlock(Flock flock) {
		this.flock = flock;
	}

	public abstract void apply(List<Boid> boids);
}
