package boids.behaviors;

import boids.Boid;
import processing.core.PVector;
import scenes.Mod;

import java.util.List;

public class FlowFieldBehavior extends Behavior {
	@Mod(min = 0, max = 10, defaultValue = 1)
	public float flowFieldWeight = 1;

	@Mod(min = 0, max = 1, defaultValue = 0.5f)
	public float maxFlowFieldForce = 0.5f;

	@Mod(min = 0, max = 10, defaultValue = 1f)
	private float maxFlowFieldSpeed = 1;

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();
		for (Boid b : boids) {
			applyFlowField(getFlock().getFlowField(), b);
		}
	}

	// We accumulate a new acceleration each time based on three rules
	void applyFlowField(PVector[] flowField, Boid b) {
		PVector desired = getFlock().getFlowVectorUnderCoords(b.getPosition().x, b.getPosition().y).copy();
		desired.mult(maxFlowFieldSpeed);

		PVector steer = PVector.sub(desired, b.getVelocity());
		steer.limit(maxFlowFieldForce);

		steer.mult(flowFieldWeight);

		b.applyForce(steer);
	}
}