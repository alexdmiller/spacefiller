package boids.tools;

import boids.Flock;
import processing.core.PVector;
import scenes.SceneTool;

public class FlowFieldTool extends SceneTool {
	private Flock flock;

	public FlowFieldTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		PVector v = flock.getFlowVectorUnderCoords(mouseX, mouseY);
		v.set(20, 20);
	}

	@Override
	public void keyDown(char key) {

	}

	@Override
	public String toString() {
		return "FLOW";
	}

}
