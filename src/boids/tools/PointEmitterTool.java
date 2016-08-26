package boids.tools;

import boids.Flock;
import boids.behaviors.EmitBehavior;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import scenes.SceneTool;

public class PointEmitterTool extends SceneTool {
	private EmitBehavior emitBehavior;

	public PointEmitterTool(EmitBehavior emitBehavior) {
		this.emitBehavior = emitBehavior;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		emitBehavior.addEmitter(new PointEmitter(mouseX, mouseY, 1));
	}

	@Override
	public String toString() {
		return "POINT EM";
	}
}
