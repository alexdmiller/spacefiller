package boids;

import processing.core.PVector;

public class Magnet {
	public PVector position;
	public float strength;
	public float attractionThreshold;

	public Magnet(float x, float y, float strength, float attractionThreshold) {
		this.position = new PVector(x, y);
		this.strength = strength;
		this.attractionThreshold = attractionThreshold;
	}
}
