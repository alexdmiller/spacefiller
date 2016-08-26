package boids.behaviors;

import boids.Boid;
import boids.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 8/25/16.
 */
public class EmitBehavior extends Behavior {
	private List<Emitter> emitters;

	public EmitBehavior() {
		emitters = new ArrayList<>();
	}

	@Override
	public void apply() {
		for (Emitter e : emitters) {
			List<Boid> boids = e.emit();
			getFlock().addAllBoids(boids);
		}
	}

	public void addEmitter(Emitter e) {
		emitters.add(e);
	}

	public List<Emitter> getEmitters() {
		return emitters;
	}

	public void clearEmitters() {
		emitters.clear();
	}
}
