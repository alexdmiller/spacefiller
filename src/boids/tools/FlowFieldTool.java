package boids.tools;

import boids.Flock;
import processing.core.PVector;
import scenes.SceneTool;

public class FlowFieldTool extends SceneTool {
	private static char RANDOMIZE_FLOW_KEY = 'r';

	private Flock flock;
	private PVector lastMouse;

	public FlowFieldTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		PVector v = flock.getFlowVectorUnderCoords(mouseX, mouseY);
		v.set(20, 20);
	}

	@Override
	public void mouseDragged(float mouseX, float mouseY) {
		PVector mouse = new PVector(mouseX, mouseY);

		if (lastMouse != null) {
			PVector diff = PVector.sub(mouse, lastMouse);
			diff.limit(20);
			if (flock.getBounds().contains(mouseX, mouseY)) {
				flock.getFlowVectorUnderCoords(mouseX, mouseY).set(diff);
			}
		}

		lastMouse = mouse;
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY) {
		lastMouse = null;
		flock.notifyEntitiesUpdated();
	}

	@Override
	public void keyDown(char key) {
		if (key == RANDOMIZE_FLOW_KEY) {
			flock.randomizeFlowField();
		}
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
