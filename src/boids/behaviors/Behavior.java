package boids.behaviors;

import boids.Boid;

import java.util.List;

/**
 * Created by miller on 8/20/16.
 */
public interface Behavior {
	void apply(List<Boid> boids);
}
