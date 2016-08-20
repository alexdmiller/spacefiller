package boids;// The Flock (a list of Boid objects)

import emitter.Emitter;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Flock {
	public static final int MAX_BOIDS = 1000;

	private List<Boid> boids;
	private List<Emitter> emitters;
	private List<Magnet> magnets;

	public Flock() {
		boids = new ArrayList<>();
		emitters = new ArrayList<>();
		magnets = new ArrayList<>();
	}

	public void step(float elapsedMillis) {
		for (Emitter e : emitters) {
			List<Boid> boids = e.emit(elapsedMillis);
			addAllBoids(boids);
		}

		for (Boid b : boids) {
			for (Magnet m : magnets) {
				PVector delta = PVector.sub(m.position, b.getPosition());
				float distance = delta.mag();
				if (distance < m.attractionThreshold) {
					delta.normalize();
					delta.mult(m.strength / distance);
					b.applyForce(delta);
				}
			}

			b.run(boids);  // Passing the entire list of boids to each boid individually
		}

	}

	public void addBoid(Boid b) {
		if (boids.size() < MAX_BOIDS) {
			boids.add(b);
		}
	}

	public void addAllBoids(Collection<Boid> boids) {
		if (boids.size() + this.boids.size() <= MAX_BOIDS) {
			this.boids.addAll(boids);
		}
	}

	public List<Boid> getBoids() { return boids; }

	public void addEmitter(Emitter e) {
		emitters.add(e);
	}

	public void addMagnet(Magnet m) {
		magnets.add(m);
	}

	public List<Magnet> getMagnets() { return magnets; }

	public List<Emitter> getEmitters() {
		return emitters;
	}
}