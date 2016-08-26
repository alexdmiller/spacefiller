package boids.tools;

import boids.Flock;
import boids.behaviors.EmitBehavior;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;

public class LineEmitterTool extends SceneTool {
	private EmitBehavior emitBehavior;
	private PVector lastPoint;

	public LineEmitterTool(EmitBehavior emitBehavior) {
		this.emitBehavior = emitBehavior;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		if (lastPoint == null) {
			lastPoint = new PVector(mouseX, mouseY);
		} else {
			emitBehavior.addEmitter(new LineEmitter(lastPoint.x, lastPoint.y, mouseX, mouseY, 1));
			lastPoint = null;
		}
	}

	@Override
	public String toString() {
		return "LINE EM" + (lastPoint == null ? " - first point" : " - second point");
	}

}
