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
	private float killRadius;
	private float forceMultiplier;

	public MagnetBehavior(float killRadius) {
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

				if (distance < m.radius) {
					delta.normalize();
					delta.mult(100 * m.strength / distance * forceMultiplier);

					b.applyForce(delta);
				}

				if (closest == null || distance < closestDistance) {
					closest = m;
					closestDistance = distance;
				}
			}

			if (closest != null) {
				if (closestDistance < killRadius) {
					boidIterator.remove();
					getFlock().notifyRemoved(b);
				}
			}
		}
	}
}
