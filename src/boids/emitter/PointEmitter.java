package boids.emitter;

import boids.Boid;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class PointEmitter implements Emitter {
	private float emitChance;
	private PVector position;
	private float initialSpeed;
	private int team;

	public PointEmitter(float x, float y, float z, float emitChance, int team) {
		this.position = new PVector(x, y, z);
		this.emitChance = emitChance;
		this.initialSpeed = 10;
		this.team = team;
	}

	@Override
	public float getEmitChance() {
		return emitChance;
	}

	@Override
	public List<Boid> emit(int dimension) {
		List<Boid> boids = new ArrayList<>();

		Boid b = null;
		if (dimension == 2) {
			b = new Boid(
					(float) (position.x + Math.random() - 0.5),
					(float) (position.y + Math.random() - 0.5),
					0);
			PVector velocity = PVector.random2D();
			velocity.setMag(initialSpeed);
			b.setVelocity(velocity);
		} else {
			b = new Boid(
					(float) (position.x + Math.random() - 0.5),
					(float) (position.y + Math.random() - 0.5),
					(float) (position.z + Math.random() - 0.5));
			PVector velocity = PVector.random3D();
			velocity.setMag(initialSpeed);
			b.setVelocity(velocity);

		}
		b.setTeam(team);

		boids.add(b);
		return boids;
	}

	public PVector getPosition() {
		return position;
	}
}
