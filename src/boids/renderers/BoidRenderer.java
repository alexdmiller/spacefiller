package boids.renderers;

import boids.Boid;
import processing.core.PGraphics;

public abstract class BoidRenderer {
	protected Boid boid;
	protected boolean markedForDeath;
	protected boolean readyToDie;

	public BoidRenderer(Boid boid) {
		this.boid = boid;
	}

	public void markReadyForDeath() {
		markedForDeath = true;
		readyToDie = true;
	}

	public boolean isReadyToDie() {
		return readyToDie;
	}

	public abstract void draw(PGraphics graphics);
}
