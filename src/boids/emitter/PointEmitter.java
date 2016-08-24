package boids.emitter;

import boids.Boid;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class PointEmitter implements Emitter {
	private float emitChance;
	private PVector position;
	private PVector initialVelocity;

	public PointEmitter(float x, float y, float emitChance) {
		this.position = new PVector(x, y);
		this.emitChance = emitChance;
		this.initialVelocity = new PVector(0, 0);
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
			Boid b = new Boid((float) (position.x + Math.random() - 0.5), (float) (position.y + Math.random() - 0.5));
			b.setVelocity(initialVelocity);
			boids.add(b);
		}

		return boids;
	}

	public PVector getPosition() {
		return position;
	}
}
