package boids.behaviors;

import boids.Boid;
import boids.emitter.Emitter;
import spacefiller.remote.Mod;

import java.util.ArrayList;
import java.util.List;

public class EmitBehavior extends Behavior {
	@Mod(min = 2, max = 4, defaultValue = 2)
	public int dimension = 2;

	@Mod
	public void emit() {
		for (Emitter e : getFlock().getEmitters()) {
			List<Boid> boids = e.emit(dimension);
			getFlock().addAllBoids(boids);
		}
	}

	@Override
	public void apply() {
		List<Emitter> emitters = getFlock().getEmitters();
		if (emitters.size() > 0) {
			Emitter e = emitters.get((int) Math.floor(Math.random() * emitters.size()));
			List<Boid> boids = e.emit(dimension);
			getFlock().addAllBoids(boids);
		}
	}
}
