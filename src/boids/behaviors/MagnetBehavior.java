package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagnetBehavior extends Behavior {
	private List<Magnet> magnets;
	private boolean repelling = false;
	private float attractionThreshold;
	private float killRadius;
	private float maxForce;

	public MagnetBehavior(float attractionThreshold, float killRadius, float maxForce) {
		this.magnets = new ArrayList<>();
		this.attractionThreshold = attractionThreshold;
		this.killRadius = killRadius;
		this.maxForce = maxForce;
	}

	public boolean isRepelling() {
		return repelling;
	}

	public void setRepelling(boolean repelling) {
		this.repelling = repelling;
	}

	public void addMagnet(float x, float y, float strength) {
		this.magnets.add(new Magnet(new PVector(x, y), strength));
	}

	public List<Magnet> getMagnets() {
		return magnets;
	}

	@Override
	public void apply(List<Boid> boids) {
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
					if (repelling) {
						PVector delta = PVector.sub(closest.position, b.getPosition());
						delta.mult(-1);
						delta.add(b.getPosition());
						PVector steer = BoidUtils.seek(b, delta, closest.strength, maxForce);  // Steer towards the position
						b.applyForce(steer);
					} else {
						PVector steer = BoidUtils.seek(b, closest.position, b.getMaxSpeed(), maxForce);  // Steer towards the position
						b.applyForce(steer);
					}
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
