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

	public PointEmitter(float x, float y, float emitChance, int team) {
		this.position = new PVector(x, y);
		this.emitChance = emitChance;
		this.initialSpeed = 10;
		this.team = team;
	}

	@Override
	public float getEmitChance() {
		return emitChance;
	}

	@Override
	public List<Boid> emit() {
		List<Boid> boids = new ArrayList<>();
		Boid b = new Boid((float) (position.x + Math.random() - 0.5), (float) (position.y + Math.random() - 0.5));
		b.setTeam(team);

		PVector velocity = PVector.fromAngle((float) (Math.random() * Math.PI * 2));
		velocity.setMag(initialSpeed);

		b.setVelocity(velocity);
		boids.add(b);
		return boids;
	}

	public PVector getPosition() {
		return position;
	}
}
