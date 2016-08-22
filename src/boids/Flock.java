package boids;// The Flock (a list of Boid objects)

import boids.behaviors.Behavior;
import boids.emitter.Emitter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Flock {
	public static final int MAX_BOIDS = 1000;

	private Rectangle bounds;
	private List<Boid> boids;
	private List<Emitter> emitters;
	private List<Behavior> behaviors;
	private List<FlockEventListener> eventListeners;

	public Flock(int x, int y, int width, int height) {
		bounds = new Rectangle(x, y, width, height);
		boids = new ArrayList<>();
		emitters = new ArrayList<>();
		behaviors = new ArrayList<>();
		eventListeners = new ArrayList<>();
	}

	public void step(float elapsedMillis) {
		for (Emitter e : emitters) {
			List<Boid> boids = e.emit(elapsedMillis);
			addAllBoids(boids);
		}

		Iterator<Boid> boidIterator = boids.iterator();
		while (boidIterator.hasNext()) {
			Boid b = boidIterator.next();
			if (!bounds.contains(b.getPosition().x, b.getPosition().y)) {
				boidIterator.remove();
				notifyRemoved(b);
			}
		}

		for (Behavior b : behaviors) {
			b.apply(getBoids());
		}

		for (Boid b : boids) {
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

	public void notifyRemoved(Boid b) {
		for (FlockEventListener listener : eventListeners) {
			listener.boidRemoved(b);
		}
	}

	public List<Boid> getBoids() { return boids; }

	public void addEmitter(Emitter e) {
		emitters.add(e);
	}

	public List<Emitter> getEmitters() {
		return emitters;
	}

	public void addBehavior(Behavior behavior) {
		behavior.setFlock(this);
		behaviors.add(behavior);
	}

	public void addEventListener(FlockEventListener listener) {
		eventListeners.add(listener);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}
}