package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import boids.Magnet;
import processing.core.PVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagnetBehavior extends Behavior {
	private float attractionThreshold;
	private float killRadius;
	private float forceMultiplier;

	public MagnetBehavior(float attractionThreshold, float killRadius) {
		this.attractionThreshold = attractionThreshold;
		this.killRadius = killRadius;
		this.forceMultiplier = 1;
	}

	public float getForceMultiplier() {
		return forceMultiplier;
	}

	public void setForceMultiplier(float forceMultiplier) {
		this.forceMultiplier = forceMultiplier;
	}

	@Override
	public void apply() {
		Iterator<Boid> boidIterator = getFlock().getBoidsIterator();
		while (boidIterator.hasNext()) {
			Boid b = boidIterator.next();

			Magnet closest = null;
			float closestDistance = 0;
			for (Magnet m : getFlock().getMagnets()) {
				PVector delta = PVector.sub(m.position, b.getPosition());
				float distance = delta.mag();
				if (closest == null || distance < closestDistance) {
					closest = m;
					closestDistance = distance;
				}
			}

			if (closest != null) {
				if (closestDistance < killRadius) {
					boidIterator.remove();
					getFlock().notifyRemoved(b);
				} else if (closestDistance < attractionThreshold) {
					PVector delta = PVector.sub(closest.position, b.getPosition());

					delta.normalize();
					delta.mult(100 * closest.strength / closestDistance * forceMultiplier);
					b.applyForce(delta);
				}
			}
		}
	}
}
