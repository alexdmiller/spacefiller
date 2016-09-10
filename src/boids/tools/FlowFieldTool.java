package boids.tools;

import boids.Flock;
import processing.core.PVector;
import scenes.Scene;
import scenes.SceneTool;

public class FlowFieldTool extends SceneTool {
	private static final float NOISE_SCALE = 2;
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
		float shift = (float) Math.random() * 10;
		if (key == RANDOMIZE_FLOW_KEY) {
			for (int x = 0; x < flock.getFlowFieldWidth(); x++) {
				for (int y = 0; y < flock.getFlowFieldHeight(); y++) {
					float r = Scene.getInstance().noise(
							(float) x / flock.getFlowFieldWidth() * NOISE_SCALE,
							(float) y / flock.getFlowFieldHeight() * NOISE_SCALE,
							shift);
					float theta = (float) (r * Math.PI * 8);
					PVector f = PVector.fromAngle(theta);
					f.setMag(10);
					flock.getFlowVector(x, y).set(f);
				}
			}
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
