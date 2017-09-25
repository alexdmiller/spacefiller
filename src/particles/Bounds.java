package particles;

import processing.core.PVector;

import java.io.Serializable;

public class Bounds implements Serializable {
	float width, height, depth;

	public Bounds(float width, float height, float depth) {
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public Bounds(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public Bounds(float size) {
		width = height = depth = size;
	}

	public boolean contains(float x, float y) {
		return 	(x > -width/2 && x < width/2) &&
				(y > -height/2 && y < height/2);
	}

	public boolean contains(float x, float y, float z) {
		return 	(x >= -width/2 && x <= width/2) &&
				(y >= -height/2 && y <= height/2) &&
				(z >= -depth/2 && z <= depth/2);
	}

	public boolean contains(PVector p) {
		return contains(p.x, p.y, p.z);
	}

	public void constrain(Particle p) {
		if (p.position.x < -width / 2) {
			p.position.x = -width / 2;
			p.velocity.x *= -1;
		} else if (p.position.x > width / 2) {
			p.position.x = width / 2;
			p.velocity.x *= -1;
		}

		if (p.position.y < -height / 2) {
			p.position.y = -height / 2;
			p.velocity.y *= -1;
		} else if (p.position.y > height / 2) {
			p.position.y = height / 2;
			p.velocity.y *= -1;
		}

		if (p.position.z < -depth / 2) {
			p.position.z = -depth / 2;
			p.velocity.z *= -1;
		} else if (p.position.z > depth / 2) {
			p.position.z = depth / 2;
			p.velocity.z *= -1;
		}

	}

	public PVector getRandomPointInside(int dimension) {
		return new PVector(
				(float) Math.random() * width - width / 2,
				(float) Math.random() * height - height / 2,
				dimension == 3 ? (float) Math.random() * height - height / 2 : 0);
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

	public void setSize(float newSize) {
		width = height = depth = newSize;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setDepth(float depth) {
		this.depth = depth;
	}
}
