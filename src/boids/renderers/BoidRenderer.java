package boids.renderers;

import boids.Boid;
import processing.core.PGraphics;

public abstract class BoidRenderer {
	protected Boid boid;

	public BoidRenderer(Boid boid) {
		this.boid = boid;
	}

	public abstract void draw(PGraphics graphics);
}
