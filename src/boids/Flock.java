package boids;

import boids.behaviors.Behavior;
import boids.emitter.Emitter;
import common.*;
import javafx.util.Pair;
import processing.core.PVector;
import modulation.Mod;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Flock implements Serializable {
	@Mod
	public ScalarField maxSpeed = new WavePropertyField();

	@Mod(min = 0, max = 700, defaultValue = 300)
	public float maxBoids = 300;

	@Mod
	public StoredVectorField flowField;

	private transient List<BoidEventListener> boidEventListeners;
	private transient List<EntityEventListener> entityEventListeners;
	private transient List<Behavior> behaviors;
	private transient Rectangle bounds;
	private transient List<Boid> boids;

	private List<Emitter> emitters;
	private List<Pair<PVector, PVector>> pathSegments;
	private List<Magnet> magnets;

	private float time;

	public Flock(int x, int y, int width, int height) {
		bounds = new Rectangle(x, y, width, height);
		boids = new ArrayList<>();
		emitters = new ArrayList<>();
		pathSegments = new ArrayList<>();
		magnets = new ArrayList<>();
		behaviors = new ArrayList<>();
		boidEventListeners = new ArrayList<>();
		entityEventListeners = new ArrayList<>();
		flowField = new StoredVectorField(bounds, 100);
	}

	public int getWidth() {
		return bounds.width;
	}

	public int getHeight() {
		return bounds.height;
	}

	public void step() {
		time++;
		synchronized (boids) {
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
				b.update(getMaxSpeed(b.getPosition().x, b.getPosition().y));
			}
		}
	}

	public void addBoid(Boid b) {
		if (boids.size() < maxBoids) {
			synchronized (boids) {
				boids.add(b);
			}
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

	@Mod
	public void clearBoids() {
		synchronized (boids) {
			for (Boid b : boids) {
				notifyRemoved(b);
			}
			boids.clear();
		}
	}

	public void notifyRemoved(Boid b) {
		for (BoidEventListener listener : boidEventListeners) {
			listener.boidRemoved(b);
		}
	}

	public List<Boid> getBoids() { return new ArrayList<>(boids); }

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
		return new ArrayList<>(emitters);
	}

	public void addEmitter(Emitter e) {
		emitters.add(e);
		notifyEntitiesUpdated();
	}

	public List<Pair<PVector, PVector>> getPathSegments() {
		return new ArrayList<>(pathSegments);
	}

	public void addPathSegment(PVector p1, PVector p2) {
		this.pathSegments.add(new Pair<>(p1, p2));
		notifyEntitiesUpdated();
	}

	public List<Magnet> getMagnets() {
		return magnets;
	}

	public void addMagnet(Magnet magnet) {
		this.magnets.add(magnet);
		notifyEntitiesUpdated();
	}

	public void notifyEntitiesUpdated() {
		for (EntityEventListener listener : entityEventListeners) {
			listener.entitiesUpdated();
		}
	}

	public void clearEntities() {
		clearMagnets();
		clearPathSegments();
		clearEmitters();
		clearFlowField();
	}

	public void clearMagnets() {
		magnets.clear();
		notifyEntitiesUpdated();
	}

	public void clearPathSegments() {
		pathSegments.clear();
		notifyEntitiesUpdated();
	}

	public void clearEmitters() {
		emitters.clear();
		notifyEntitiesUpdated();
	}

	public void clearFlowField() {
		flowField.zero();
		notifyEntitiesUpdated();
	}

	public void copyEntitiesFrom(Flock other) {
		magnets.clear();
		pathSegments.clear();
		emitters.clear();

		flowField.set(other.flowField);

		magnets.addAll(other.magnets);
		pathSegments.addAll(other.pathSegments);
		emitters.addAll(other.emitters);
	}

	public void addEntityEventListener(EntityEventListener listener) {
		entityEventListeners.add(listener);
	}

	public float getMaxSpeed(float x, float y) {
		return maxSpeed.at(x, y, time);
	}

	public Iterator<Boid> getBoidsIterator() {
		return boids.iterator();
	}

	public VectorField getFlowField() {
		return flowField;
	}

	public PVector getFlowVectorUnderCoords(float x, float y) {
		return flowField.at(x, y, time);
	}

	public int getFlowFieldWidth() {
		return flowField.getGridWidth();
	}

	public int getFlowFieldHeight() {
		return flowField.getGridHeight();
	}
}