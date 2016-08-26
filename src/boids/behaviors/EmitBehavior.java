package boids.behaviors;

import boids.Boid;
import boids.emitter.Emitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 8/25/16.
 */
public class EmitBehavior extends Behavior {
	@Override
	public void apply() {
		for (Emitter e : getFlock().getEmitters()) {
			List<Boid> boids = e.emit();
			getFlock().addAllBoids(boids);
		}
	}
}
