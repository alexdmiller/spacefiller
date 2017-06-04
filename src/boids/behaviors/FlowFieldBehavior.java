package boids.behaviors;

import boids.Boid;
import common.VectorField;
import processing.core.PVector;
import modulation.Mod;

import java.util.List;

public class FlowFieldBehavior extends Behavior {
	@Mod(min = 0, max = 10, defaultValue = 0)
	public float flowFieldWeight = 0;

	@Mod(min = 0, max = 1, defaultValue = 0.5f)
	public float maxFlowFieldForce = 0.5f;

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();
		for (Boid b : boids) {
			applyFlowField(b);
		}
	}

	// We accumulate a new acceleration each time based on three rules
	void applyFlowField(Boid b) {
		PVector pos = b.getPosition();
		PVector desired = getFlock().getFlowVectorUnderCoords(pos).copy();
		desired.limit(getFlock().getMaxSpeed(pos));

		PVector steer = PVector.sub(desired, b.getVelocity());
		steer.limit(maxFlowFieldForce);

		steer.mult(flowFieldWeight);

		b.applyForce(steer);
	}
}
