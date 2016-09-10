package boids.behaviors;

import boids.Boid;
import boids.Flock;
import modulation.Mod;
import processing.core.PVector;

import java.util.List;

public class WiggleBehavior extends Behavior {
	@Mod(min = 0, max = 10, defaultValue = 1)
	public float wiggleAmplitudeMultiplier = 1;

	@Mod(min = 0, max = 10, defaultValue = 1)
	public float wiggleStepMultiplier;

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();

		for (Boid b : boids) {
			PVector wiggleDirection = b.getVelocity().copy().rotate((float) Math.PI / 2f);

			if (!b.hasUserData("wiggleStep")) {
				b.setUserData("wiggleStep", 0f);
			}
			float wiggleStep = (float) b.getUserData("wiggleStep");

			wiggleDirection.setMag((float) Math.sin(wiggleStep) * b.getVelocity().mag() * wiggleAmplitudeMultiplier);
			b.getPosition().add(wiggleDirection);
			wiggleStep += b.getVelocity().mag() * wiggleStepMultiplier;
			b.setUserData("wiggleStep", wiggleStep);
		}
	}
}
