package boids.emitter;

import boids.Boid;

import java.util.List;

public interface Emitter {
	List<Boid> emit(float ellapsed);
}
