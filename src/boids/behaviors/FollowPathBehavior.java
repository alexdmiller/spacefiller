package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import processing.core.PVector;

import java.util.List;

public class FollowPathBehavior extends Behavior {
	private PVector start;
	private PVector end;
	private float radius;

	public FollowPathBehavior(PVector start, PVector end, float radius) {
		this.start = start;
		this.end = end;
		this.radius = radius;
	}

	public PVector getStart() {
		return start;
	}

	public PVector getEnd() {
		return end;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public void apply(List<Boid> boids) {
		for (Boid boid : boids) {
			PVector predictedPosition = PVector.add(boid.getPosition(), boid.getVelocity());
			PVector a = PVector.sub(predictedPosition, start);
			PVector b = PVector.sub(end, start);
			float segmentLength = b.mag();

			float theta = PVector.angleBetween(a, b);

			float d = (float) (a.mag() * Math.cos(theta));
			b.setMag(d);
			b.limit(segmentLength);

			PVector normalPoint = PVector.add(start, b);
			float distance = PVector.sub(boid.getPosition(), normalPoint).mag();

			if (distance > radius) {
				PVector steer = BoidUtils.seek(boid, normalPoint, boid.getMaxSpeed(), 1);
				boid.applyForce(steer);
			}
		}
	}
}
