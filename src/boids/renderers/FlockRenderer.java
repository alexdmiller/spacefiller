package boids.renderers;

import boids.Flock;
import processing.core.PGraphics;

import java.util.ArrayList;

public abstract class FlockRenderer {
	protected Flock flock;

	public FlockRenderer(Flock flock) {
		this.flock = flock;
	}
	public void clear() {}
	public abstract void render(PGraphics canvas);
}
