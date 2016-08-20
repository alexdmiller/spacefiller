package boids;// The Flock (a list of Boid objects)

import boids.behaviors.Behavior;
import boids.behaviors.FlockBehavior;
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
	private List<Behavior> behaviors;
	private List<FlockEventListener> eventListeners;

	public Flock() {
		boids = new ArrayList<>();
		emitters = new ArrayList<>();
		magnets = new ArrayList<>();
		behaviors = new ArrayList<>();
		eventListeners = new ArrayList<>();
	}

	public void step(float elapsedMillis) {
		for (Emitter e : emitters) {
			List<Boid> boids = e.emit(elapsedMillis);
			addAllBoids(boids);
		}

		for (Behavior b : behaviors) {
			b.apply(getBoids());
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

			b.update();
		}
	}

	public void addBoid(Boid b) {
		if (boids.size() < MAX_BOIDS) {
			boids.add(b);
			for (FlockEventListener listener : eventListeners) {
				listener.boidAdded(b);
			}
		}
	}

	public void addAllBoids(Collection<Boid> boids) {
		for (Boid b : boids) {
			addBoid(b);
		}
	}

	public void removeBoid(Boid b) {
		// TODO
	}

	public List<Boid> getBoids() { return boids; }

	public void addEmitter(Emitter e) {
		emitters.add(e);
	}

	public List<Emitter> getEmitters() {
		return emitters;
	}

	public void addMagnet(Magnet m) {
		magnets.add(m);
	}

	public List<Magnet> getMagnets() { return magnets; }

	public void addBehavior(Behavior behavior) {
		behaviors.add(behavior);
	}

	public void addEventListener(FlockEventListener listener) {
		eventListeners.add(listener);
	}
}