package boids.emitter;

import boids.Boid;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class LineEmitter implements Emitter {
	private float emitChance;
	private PVector p1;
	private PVector p2;
	private PVector initialVelocity;

	public LineEmitter(float x1, float y1, float x2, float y2, float emitChance) {
		this.p1 = new PVector(x1, y1);
		this.p2 = new PVector(x2, y2);
		this.emitChance = emitChance;
		this.initialVelocity = new PVector(0, 0);
	}

	public PVector getP1() {
		return p1;
	}

	public PVector getP2() {
		return p2;
	}

	public PVector getInitialVelocity() {
		return initialVelocity;
	}

	public void setInitialVelocity(float x, float y) {
		this.initialVelocity = new PVector(x, y);
	}

	@Override
	public List<Boid> emit() {
		List<Boid> boids = new ArrayList<>();

		if (Math.random() < emitChance) {
			PVector delta = PVector.sub(p2, p1);
			float distOnLine = (float) Math.random() * delta.mag();

			delta.setMag(distOnLine);
			delta.add(p1);

			Boid b = new Boid(delta.x, delta.y);
			b.setVelocity(initialVelocity);
			boids.add(b);
		}

		return boids;
	}
}
