package boids.behaviors;

import boids.Boid;
import boids.Flock;
import processing.core.PVector;

import java.util.List;

public class WiggleBehavior extends Behavior {
	private float wiggleAmplitudeMultiplier;
	private float wiggleStepMultiplier;

	public WiggleBehavior(float wiggleAmplitudeMultiplier, float wiggleStepMultiplier) {
		this.wiggleAmplitudeMultiplier = wiggleAmplitudeMultiplier;
		this.wiggleStepMultiplier = wiggleStepMultiplier;
	}

	@Override
	public void apply(List<Boid> boids) {
		for (Boid b : boids) {
			PVector wiggleDirection = b.getVelocity().copy().rotate((float) Math.PI / 2f);

			if (!b.hasUserData("wiggleStep")) {
				b.setUserData("wiggleStep", 0f);
			}
			float wiggleStep = (float) b.getUserData("wiggleStep");

			wiggleDirection.setMag((float) Math.sin(wiggleStep) * b.getVelocity().mag() * wiggleAmplitudeMultiplier);
			b.getPosition().add(wiggleDirection);
			wiggleStep += b.getAcceleration().mag() * wiggleStepMultiplier;
			b.setUserData("wiggleStep", wiggleStep);
		}
	}
}
