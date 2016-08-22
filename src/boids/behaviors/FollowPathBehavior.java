package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class FollowPathBehavior extends Behavior {
	private List<PVector> points;
	private float radius;
	private float maxForce;

	public FollowPathBehavior(float radius, float maxForce) {
		this.points = new ArrayList<>();
		this.radius = radius;
		this.maxForce = maxForce;
	}

	public void addPoint(float x, float y) {
		this.points.add(new PVector(x, y));
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public void apply(List<Boid> boids) {
		for (Boid boid : boids) {
			PVector last = null;
			PVector closestNormalPoint = null;
			float closestDistance = 0;

			for (PVector p : points) {
				if (last != null) {
					PVector normalPoint = getNormalPoint(last, p, boid);
					float distance = PVector.sub(boid.getPosition(), normalPoint).mag();
					if (closestNormalPoint == null || distance < closestDistance) {
						closestNormalPoint = normalPoint;
						closestDistance = distance;
					}
				}

				last = p;
			}

			if (closestDistance > radius) {
				PVector steer = BoidUtils.seek(boid, closestNormalPoint, boid.getMaxSpeed(), maxForce);
				boid.applyForce(steer);
			}
		}
	}

	private PVector getNormalPoint(PVector start, PVector end, Boid boid) {
		PVector predictedPosition = PVector.add(boid.getPosition(), boid.getVelocity());
		PVector a = PVector.sub(predictedPosition, start);
		PVector b = PVector.sub(end, start);
		float segmentLength = b.mag();

		float theta = PVector.angleBetween(a, b);

		float d = (float) (a.mag() * Math.cos(theta));
		d = Math.max(Math.min(d, segmentLength), 0);
		b.setMag(d);

		PVector normalPoint = PVector.add(start, b);
		return normalPoint;
	}

	public List<PVector> getPoints() {
		return points;
	}

	public void clearPoints() {
		points.clear();
	}
}
