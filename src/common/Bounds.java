package common;

import processing.core.PVector;

import java.io.Serializable;

public class Bounds implements Serializable {
	float width, height, depth;

	public Bounds(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public boolean contains(float x, float y, float z) {
		return 	(x > -width/2 && x < width/2) &&
				(y > -height/2 && y < height/2) &&
				(z > -depth/2 && z < depth/2);
	}

	public boolean contains(PVector p) {
		return contains(p.x, p.y, p.z);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getDepth() {
		return depth;
	}
}
