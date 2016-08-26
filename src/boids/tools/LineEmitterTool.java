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
		if (lastPoint == null) {
			lastPoint = new PVector(mouseX, mouseY);
		} else {
			flock.addEmitter(new LineEmitter(lastPoint.x, lastPoint.y, mouseX, mouseY, 1));
			lastPoint = null;
		}
	}

	@Override
	public String toString() {
		return "LINE EM" + (lastPoint == null ? " - first point" : " - second point");
	}
}
