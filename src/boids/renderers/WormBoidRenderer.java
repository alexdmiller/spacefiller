package boids.renderers;

import boids.Boid;
import boids.renderers.BoidRenderer;
import com.google.common.collect.EvictingQueue;
import processing.core.PGraphics;
import processing.core.PVector;

public class WormBoidRenderer extends BoidRenderer {
	public static final int HISTORY_SIZE = 20;
	private EvictingQueue<PVector> history;

	public WormBoidRenderer(Boid boid) {
		super(boid);
		this.history = EvictingQueue.create(HISTORY_SIZE);
	}

	@Override
	public void markReadyForDeath() {
		markedForDeath = true;
	}

	@Override
	public void draw(PGraphics graphics) {
		if (markedForDeath) {
			if (!history.isEmpty()) {
				history.remove();
			}
			if (history.size() == 0) {
				readyToDie = true;
			}
		} else {
			history.add(boid.getPosition().copy());
		}
		PVector last = null;
		for (PVector p : history) {
			if (last != null) {
				graphics.line(last.x, last.y, p.x, p.y);
			}
			last = p;
		}
	}
}
