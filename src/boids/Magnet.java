package boids;

import processing.core.PVector;
import java.io.Serializable;

public class Magnet implements Serializable {
	public PVector position;
	public float strength;

	public Magnet(PVector position, float strength) {
		this.position = position;
		this.strength = strength;
	}
}