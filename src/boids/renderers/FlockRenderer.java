package boids.renderers;

import boids.Flock;
import processing.core.PGraphics;

import java.util.ArrayList;

public abstract class FlockRenderer {
	protected Flock flock;
	protected PGraphics canvas;

	public FlockRenderer(Flock flock, PGraphics canvas) {
		this.flock = flock;
		this.canvas = canvas;
	}

	public abstract void render();
}
