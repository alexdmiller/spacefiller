package boids.tools;

import boids.Flock;
import boids.behaviors.EmitBehavior;
import boids.emitter.Emitter;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;
import scenes.Worms;

public class LineEmitterTool extends SceneTool {
	private Flock flock;
	private PVector lastPoint;

	public LineEmitterTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		lastPoint = new PVector(mouseX, mouseY);
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY) {
		flock.addEmitter(new LineEmitter(lastPoint.x, lastPoint.y, mouseX, mouseY, 1));
		lastPoint = null;
	}

	@Override
	public void render(PGraphics graphics, float mouseX, float mouseY, boolean mousePressed) {
		graphics.stroke(0, 255, 0);
		if (lastPoint != null) {
			graphics.line(lastPoint.x, lastPoint.y, mouseX, mouseY);
		}
	}

	@Override
	public String toString() {
		return "LINE";
	}

	@Override
	public void clear() {
		flock.clearEmitters();
	}
}
