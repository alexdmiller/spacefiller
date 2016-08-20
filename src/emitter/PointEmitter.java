package emitter;

import boids.Boid;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class PointEmitter implements Emitter {
	private float emitChance;
	private PVector position;

	public PointEmitter(float x, float y, float emitChance) {
		this.position = new PVector(x, y);
		this.emitChance = emitChance;
	}

	@Override
	public List<Boid> emit(float ellapsed) {
		List<Boid> boids = new ArrayList<>();

		if (Math.random() < emitChance) {
			Boid b = new Boid(position.x, position.y);
			boids.add(b);
		}

		return boids;
	}

	public PVector getPosition() {
		return position;
	}
}
