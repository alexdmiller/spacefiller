package lusio;

import processing.core.PGraphics;

/**
 * Created by miller on 7/12/17.
 */
public class SceneGenerator {
	private float width;
	private float height;
	private float x;
	private float y;
	
	public SceneGenerator(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public void draw(PGraphics graphics) {
		
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}
