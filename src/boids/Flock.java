package boids;

import boids.behaviors.Behavior;
import boids.emitter.Emitter;
import javafx.util.Pair;
import processing.core.PVector;
import modulation.Mod;
import scenes.Scene;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Flock implements Serializable {
	public static final int FLOW_FIELD_RESOLUTION = 50;
	private static final float NOISE_SCALE = 2;

	@Mod(min = 0, max = 10, defaultValue = 3)
	public float maxSpeed = 3;

	@Mod(min = 0, max = 1000, defaultValue = 700)
	public float maxBoids = 700;

	private transient List<BoidEventListener> boidEventListeners;
	private transient List<EntityEventListener> entityEventListeners;
	private transient List<Behavior> behaviors;
	private transient Rectangle bounds;
	private transient List<Boid> boids;

	private List<Emitter> emitters;
	private List<Pair<PVector, PVector>> pathSegments;
	private List<Magnet> magnets;
	private PVector[] flowField;
	private int width;
	private int height;

	public Flock(int x, int y, int width, int height) {
		bounds = new Rectangle(x, y, width, height);
		boids = new ArrayList<>();
		emitters = new ArrayList<>();
		pathSegments = new ArrayList<>();
		magnets = new ArrayList<>();
		behaviors = new ArrayList<>();
		boidEventListeners = new ArrayList<>();
		entityEventListeners = new ArrayList<>();
		this.width = width;
		this.height = height;

		flowField = new PVector[(width * height) / FLOW_FIELD_RESOLUTION];
		for (int i = 0; i < flowField.length; i++) {
			flowField[i] = new PVector(0, 0);
		}
	}

	public void step() {
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
				b.update(maxSpeed);
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
		for (int i = 0; i < flowField.length; i++) {
			flowField[i].set(0, 0);
		}
		notifyEntitiesUpdated();
	}

	public void copyEntitiesFrom(Flock other) {
		magnets.clear();
		pathSegments.clear();
		emitters.clear();

		for (int i = 0; i < flowField.length; i++) {
			flowField[i].set(other.getFlowVector(i));
		}

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

	public Iterator<Boid> getBoidsIterator() {
		return boids.iterator();
	}

	public PVector[] getFlowField() {
		return flowField;
	}

	public PVector getFlowVectorUnderCoords(float x, float y) {
		int cellX = (int) (x - bounds.x) / Flock.FLOW_FIELD_RESOLUTION;
		int cellY = (int) (y - bounds.y) / Flock.FLOW_FIELD_RESOLUTION;
		return getFlowVector(cellX, cellY);
	}

	public PVector getFlowVector(int x, int y) {
		int i = y * bounds.width / Flock.FLOW_FIELD_RESOLUTION + x;
		if (i < flowField.length && i > 0) {
			return flowField[y * bounds.width / Flock.FLOW_FIELD_RESOLUTION + x];
		} else {
			return new PVector(0, 0);
		}
	}

	public PVector getFlowVector(int i) {
		return flowField[i];
	}

	public int getFlowFieldWidth() {
		return bounds.width / Flock.FLOW_FIELD_RESOLUTION;
	}

	public int getFlowFieldHeight() {
		return bounds.height / Flock.FLOW_FIELD_RESOLUTION;
	}

	@Mod
	public void randomizeFlowField() {
		float shift = (float) Math.random() * 10;
		for (int x = 0; x < getFlowFieldWidth(); x++) {
			for (int y = 0; y < getFlowFieldHeight(); y++) {
				float r = Scene.getInstance().noise(
						(float) x / getFlowFieldWidth() * NOISE_SCALE,
						(float) y / getFlowFieldHeight() * NOISE_SCALE,
						shift);
				float theta = (float) (r * Math.PI * 8);
				PVector f = PVector.fromAngle(theta);
				f.setMag(10);
				getFlowVector(x, y).set(f);
			}
		}
	}
}