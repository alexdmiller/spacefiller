package boids;

import processing.core.PGraphics;

public interface BoidRenderer {
	void draw(Boid boid, PGraphics graphics);
}
