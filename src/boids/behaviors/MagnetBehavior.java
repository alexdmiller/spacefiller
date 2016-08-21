package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MagnetBehavior extends Behavior {
	private List<PVector> magnets;
	private boolean repelling = false;
	private float strength;
	private float attractionThreshold;
	private float killRadius;
	private float maxForce;

	public MagnetBehavior(float strength, float attractionThreshold, float killRadius, float maxForce) {
		this.magnets = new ArrayList<>();
		this.strength = strength;
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

	public void addMagnet(float x, float y) {
		this.magnets.add(new PVector(x, y));
	}

	public List<PVector> getMagnets() {
		return magnets;
	}

	@Override
	public void apply(List<Boid> boids) {
		Iterator<Boid> boidIterator = boids.iterator();
		while(boidIterator.hasNext()) {
			Boid b = boidIterator.next();

			PVector closest = null;
			float closestDistance = 0;
			for (PVector m : magnets) {
				PVector delta = PVector.sub(m, b.getPosition());
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
						PVector delta = PVector.sub(closest, b.getPosition());
						delta.mult(-1);
						delta.add(b.getPosition());
						PVector steer = BoidUtils.seek(b, delta, Math.min(5000 / delta.mag(), b.getMaxSpeed()), maxForce);  // Steer towards the position
						b.applyForce(steer);
					} else {
						PVector steer = BoidUtils.seek(b, closest, b.getMaxSpeed(), maxForce);  // Steer towards the position
						b.applyForce(steer);
					}

				}
			}
		}
	}

}
