package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagnetBehavior extends Behavior {
	private List<Magnet> magnets;
	private float attractionThreshold;
	private float killRadius;
	private float forceMultiplier;

	public MagnetBehavior(float attractionThreshold, float killRadius) {
		this.magnets = new ArrayList<>();
		this.attractionThreshold = attractionThreshold;
		this.killRadius = killRadius;
		this.forceMultiplier = 1;
	}

	public void addMagnet(float x, float y, float strength) {
		this.magnets.add(new Magnet(new PVector(x, y), strength));
	}

	public List<Magnet> getMagnets() {
		return magnets;
	}

	public float getForceMultiplier() {
		return forceMultiplier;
	}

	public void setForceMultiplier(float forceMultiplier) {
		this.forceMultiplier = forceMultiplier;
	}

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();

		Iterator<Boid> boidIterator = boids.iterator();
		while(boidIterator.hasNext()) {
			Boid b = boidIterator.next();

			Magnet closest = null;
			float closestDistance = 0;
			for (Magnet m : magnets) {
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
					break;
				} else if (closestDistance < attractionThreshold) {
					PVector delta = PVector.sub(closest.position, b.getPosition());

					delta.normalize();
					delta.mult(100 * closest.strength / closestDistance * forceMultiplier);
					b.applyForce(delta);
				}
			}
		}
	}

	public void clearMagnets() {
		magnets.clear();
	}

	public static class Magnet {
		public PVector position;
		public float strength;

		public Magnet(PVector position, float strength) {
			this.position = position;
			this.strength = strength;
		}
	}
}
