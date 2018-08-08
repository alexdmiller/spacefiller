package boids.renderers;

import boids.Boid;
import boids.renderers.BoidRenderer;
import com.google.common.collect.EvictingQueue;
import common.color.SmoothColorTheme;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.color.ColorRange;

public class WormBoidRenderer extends BoidRenderer {
	public static SmoothColorTheme colorTheme = new SmoothColorTheme(ColorRange.BRIGHT, 10, 100);
	public static final int HISTORY_SIZE = 30;
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
	public void draw(PGraphics graphics, float t) {
		graphics.stroke(boid.getTeam() == 0 ? 255 : 100);

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
		float colorIndex = t / 100f;
		for (PVector p : history) {
			if (last != null) {
				graphics.stroke(colorTheme.getColor(colorIndex).toARGB());
				graphics.line(last.x, last.y, last.z, p.x, p.y, p.z);
				colorIndex += 0.1;
			}
			last = p;
		}
	}

	@Override
	public void clear() {
		history.clear();
	}
}
