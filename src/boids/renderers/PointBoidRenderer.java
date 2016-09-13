package boids.renderers;

import boids.Boid;
import boids.renderers.BoidRenderer;
import processing.core.PGraphics;

public class PointBoidRenderer extends BoidRenderer {
	public PointBoidRenderer(Boid boid) {
		super(boid);
	}

	@Override
	public void draw(PGraphics graphics) {
		graphics.point(boid.getPosition().x, boid.getPosition().y);
	}

	public void markReadyForDeath() {
		markedForDeath = true;
		readyToDie = true;
	}
}
