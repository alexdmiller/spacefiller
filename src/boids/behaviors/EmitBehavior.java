package boids.behaviors;

import boids.Boid;
import boids.emitter.Emitter;
import modulation.Mod;

import java.util.ArrayList;
import java.util.List;

public class EmitBehavior extends Behavior {
	@Mod
	public void emit(boolean on) {
		if (on) {
			for (Emitter e : getFlock().getEmitters()) {
				List<Boid> boids = e.emit();
				getFlock().addAllBoids(boids);
			}
		}
	}

	@Override
	public void apply() {
		for (Emitter e : getFlock().getEmitters()) {
			if (Math.random() < e.getEmitChance()) {
				List<Boid> boids = e.emit();
				getFlock().addAllBoids(boids);
			}
		}
	}
}
