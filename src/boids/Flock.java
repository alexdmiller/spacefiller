package boids;

import boids.behaviors.Behavior;
import boids.emitter.Emitter;
import javafx.util.Pair;
import processing.core.PVector;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Flock implements Serializable {
	public static final int MAX_BOIDS = 500;

	private transient List<BoidEventListener> boidEventListeners;
	private transient List<EntityEventListener> entityEventListeners;
	private transient List<Behavior> behaviors;
	private transient Rectangle bounds;
	private transient List<Boid> boids;

	private List<Emitter> emitters;
	private List<Pair<PVector, PVector>> pathSegments;
	private List<Magnet> magnets;
	private float maxSpeed = 3;

	public Flock(int x, int y, int width, int height) {
		bounds = new Rectangle(x, y, width, height);
		boids = new ArrayList<>();
		behaviors = new ArrayList<>();
		boidEventListeners = new ArrayList<>();
		entityEventListeners = new ArrayList<>();
		emitters = new ArrayList<>();
		pathSegments = new ArrayList<>();
		magnets = new ArrayList<>();
	}

	public void step() {
		Iterator<Boid> boidIterator = boids.iterator();
		while (boidIterator.hasNext()) {
			Boid b = boidIterator.next();
			if (!bounds.contains(b.getPosition().x, b.getPosition().y)) {
				boidIterator.remove();
				notifyRemoved(b);
			}
		}

		for (Behavior b : behaviors) {
			b.apply();
		}

		for (Boid b : boids) {
			b.update(maxSpeed);
		}
	}

	public void addBoid(Boid b) {
		if (boids.size() < MAX_BOIDS) {
			boids.add(b);
			for (BoidEventListener listener : boidEventListeners) {
				listener.boidAdded(b);
			}
		}
	}

	public void addAllBoids(Collection<Boid> boids) {
		for (Boid b : boids) {
			addBoid(b);
		}
	}

	public void clearBoids() {
		for (Boid b : boids) {
			notifyRemoved(b);
		}
		boids.clear();
	}

	public void notifyRemoved(Boid b) {
		for (BoidEventListener listener : boidEventListeners) {
			listener.boidRemoved(b);
		}
	}

	public List<Boid> getBoids() { return boids; }

	public void addBehavior(Behavior behavior) {
		behavior.setFlock(this);
		behaviors.add(behavior);
	}

	public void addAllBehaviors(Collection<Behavior> behavior) {
		for (Behavior b : behavior) {
			addBehavior(b);
		}
	}

	public Behavior getBehavior(Class<? extends Behavior> behaviorClass) {
		for (Behavior b : getBehaviors()) {
			if (behaviorClass.isInstance(b)) {
				return b;
			}
		}
		return null;
	}

	public List<Behavior> getBehaviors() {
		return behaviors;
	}

	public void clearBehaviors() {
		behaviors.clear();
	}

	public void addBoidEventListener(BoidEventListener listener) {
		boidEventListeners.add(listener);
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public List<Emitter> getEmitters() {
		return emitters;
	}

	public void addEmitter(Emitter e) {
		emitters.add(e);
		notifyEntitiesUpdated();
	}

	public List<Pair<PVector, PVector>> getPathSegments() {
		return pathSegments;
	}

	public void addPathSegment(PVector p1, PVector p2) {
		this.pathSegments.add(new Pair<>(p1, p2));
		notifyEntitiesUpdated();
	}

	public List<Magnet> getMagnets() {
		return magnets;
	}

	public void addMagnet(float x, float y, float strength) {
		this.magnets.add(new Magnet(new PVector(x, y), strength));
		notifyEntitiesUpdated();
	}

	public void notifyEntitiesUpdated() {
		for (EntityEventListener listener : entityEventListeners) {
			listener.entitiesUpdated();
		}
	}

	public void clearEntities() {
		magnets.clear();
		pathSegments.clear();
		emitters.clear();

		notifyEntitiesUpdated();
	}

	public void copyEntitiesFrom(Flock other) {
		magnets.clear();
		pathSegments.clear();
		emitters.clear();

		magnets.addAll(other.magnets);
		pathSegments.addAll(other.pathSegments);
		emitters.addAll(other.emitters);
	}

	public void addEntityEventListener(EntityEventListener listener) {
		entityEventListeners.add(listener);
	}

	public float getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}