package boids;

import processing.core.PVector;
import java.io.Serializable;

public class Magnet implements Serializable {
	public PVector position;
	public float radius;
	public float strength;

	public Magnet(PVector position, float radius, float strength) {
		this.position = position;
		this.radius = radius;
		this.strength = strength;
	}
}