package boids.tools;

import boids.Flock;
import boids.emitter.PointEmitter;
import scenes.SceneTool;

public class PointEmitterTool extends SceneTool {
	private Flock flock;

	public PointEmitterTool(Flock emitBehavior) {
		this.flock = emitBehavior;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		flock.addEmitter(new PointEmitter(mouseX, mouseY, 1));
	}

	@Override
	public String toString() {
		return "POINT EM";
	}
}
