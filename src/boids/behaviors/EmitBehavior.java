package boids.behaviors;

import boids.Boid;
import boids.emitter.Emitter;
import modulation.Mod;

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
		for (Emitter e : getFlock().getEmitters()) {
			if (Math.random() < e.getEmitChance()) {
				List<Boid> boids = e.emit(dimension);
				getFlock().addAllBoids(boids);
			}
		}
	}
}
