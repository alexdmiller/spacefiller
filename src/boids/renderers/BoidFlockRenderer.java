package boids.renderers;

import boids.Boid;
import boids.Flock;
import boids.BoidEventListener;
import processing.core.PGraphics;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * A FlockRenderer that delegates rendering individual boids to a BoidRenderer class.
 * New BoidRenderers are created and destroyed as boids are added / removed from the
 * flock.
 */
public class BoidFlockRenderer extends FlockRenderer implements BoidEventListener {
	private List<BoidRenderer> renderers;
	private Map<Boid, BoidRenderer> boidToRenderer;
	private Class<? extends BoidRenderer> rendererClass;

	public BoidFlockRenderer(Flock flock, Class<? extends BoidRenderer> rendererClass) {
		super(flock);
		this.renderers = new ArrayList<>();
		this.boidToRenderer = new LinkedHashMap<>();
		flock.addBoidEventListener(this);

		this.rendererClass = rendererClass;
	}

	@Override
	public void render(PGraphics graphics) {
		graphics.rotateY(flock.getRotation());

		synchronized (renderers) {
			Iterator<BoidRenderer> rendererIterator = renderers.iterator();
			while (rendererIterator.hasNext()) {
				BoidRenderer renderer = rendererIterator.next();
				if (renderer.isReadyToDie()) {
					rendererIterator.remove();
				}
				renderer.draw(graphics);
			}
		}
	}

	@Override
	public void clear() {
		synchronized (renderers) {
			Iterator<BoidRenderer> rendererIterator = renderers.iterator();
			while (rendererIterator.hasNext()) {
				BoidRenderer renderer = rendererIterator.next();
				renderer.clear();
			}
		}
	}

	@Override
	public void boidAdded(Boid b) {
		BoidRenderer renderer = null;
		try {
			renderer = rendererClass.getDeclaredConstructor(Boid.class).newInstance(b);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		boidToRenderer.put(b, renderer);

		synchronized (renderers) {
			renderers.add(renderer);
		}
	}

	@Override
	public void boidRemoved(Boid b) {
		BoidRenderer boidRenderer = boidToRenderer.get(b);
		boidRenderer.markReadyForDeath();
	}
}
