package boids.tools;

import boids.Flock;
import boids.emitter.PointEmitter;
import scenes.SceneTool;

public class PointEmitterTool extends SceneTool {
	private Flock emitBehavior;

	public PointEmitterTool(Flock emitBehavior) {
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
