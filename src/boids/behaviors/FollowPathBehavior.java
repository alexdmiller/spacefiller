package boids.behaviors;

import boids.Boid;
import boids.BoidUtils;
import boids.PathSegment;
import processing.core.PVector;
import spacefiller.remote.Mod;

import java.util.List;

public class FollowPathBehavior extends Behavior {
	@Mod(min = 10, max = 100, defaultValue = 20)
	public float radius = 20;

	private float maxForce;

	public FollowPathBehavior(float maxForce) {
		this.maxForce = maxForce;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public void apply() {
		List<Boid> boids = getFlock().getBoids();
		for (Boid boid : boids) {
			PVector closestNormalPoint = null;
			float closestDistance = 0;

			for (PathSegment p : getFlock().getPathSegments()) {
				if (p != null) {
					PVector normalPoint = getNormalPoint(p.p1, p.p2, boid);
					float distance = PVector.sub(boid.getPosition(), normalPoint).mag();
					if (closestNormalPoint == null || distance < closestDistance) {
						closestNormalPoint = normalPoint;
						closestDistance = distance;
					}
				}
			}

			if (closestDistance > radius) {
				PVector steer =
						BoidUtils.seek(boid, closestNormalPoint, getFlock().getMaxSpeed(boid.getPosition()), maxForce);
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
}
