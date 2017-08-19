package boids.tools;

import boids.Flock;
import processing.core.PGraphics;
import processing.core.PVector;
import sketches.SceneTool;

public class PathTool extends SceneTool {
	private Flock flock;
	private PVector lastPoint;

	public PathTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		lastPoint = new PVector(mouseX, mouseY);
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY) {
		PVector point = new PVector(mouseX, mouseY);
		flock.addPathSegment(lastPoint, point);

		lastPoint = null;
	}

	@Override
	public void render(PGraphics graphics, float mouseX, float mouseY, boolean mousePressed) {
		if (lastPoint != null) {
			graphics.line(lastPoint.x, lastPoint.y, mouseX, mouseY);
		}
	}

	@Override
	public String toString() {
		return "PATH";
	}

	@Override
	public void clear() {
		flock.clearPathSegments();
	}
}
