package boids.emitter;

import boids.Boid;

import java.io.Serializable;
import java.util.List;

public interface Emitter extends Serializable {
	List<Boid> emit();
	float getEmitChance();
}
