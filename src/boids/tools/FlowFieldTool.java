package boids.tools;

import boids.Flock;
import processing.core.PVector;
import scenes.SceneTool;

public class FlowFieldTool extends SceneTool {
	private Flock flock;
	private PVector lastMouse;

	public FlowFieldTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY) {
		lastMouse = null;
		flock.notifyEntitiesUpdated();
	}

	@Override
	public String toString() {
		return "FLOW";
	}

	@Override
	public void clear() {
		flock.clearFlowField();
	}
}
