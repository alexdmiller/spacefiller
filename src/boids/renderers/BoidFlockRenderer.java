package boids.renderers;

import boids.Boid;
import boids.Flock;
import boids.FlockEventListener;
import processing.core.PGraphics;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BoidFlockRenderer extends FlockRenderer implements FlockEventListener {
	private List<BoidRenderer> renderers;
	private Class<? extends BoidRenderer> rendererClass;

	public BoidFlockRenderer(Flock flock, Class<? extends BoidRenderer> rendererClass) {
		super(flock);
		this.renderers = new ArrayList<>();
		flock.addEventListener(this);

		this.rendererClass = rendererClass;
	}

	@Override
	public void render(PGraphics graphics) {
		graphics.stroke(255);
		graphics.strokeWeight(3);
		Iterator<BoidRenderer> rendererIterator = renderers.iterator();
		while (rendererIterator.hasNext()) {
			BoidRenderer renderer = rendererIterator.next();
			if (renderer.isReadyToDie()) {
				rendererIterator.remove();
			}
			renderer.draw(graphics);
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
		b.setUserData("renderer", renderer);
		renderers.add(renderer);
	}

	@Override
	public void boidRemoved(Boid b) {
		BoidRenderer boidRenderer = (BoidRenderer) b.getUserData("renderer");
		boidRenderer.markReadyForDeath();
	}
}
