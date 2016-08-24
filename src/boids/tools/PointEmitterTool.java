package boids.tools;

import boids.Flock;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import scenes.SceneTool;

public class PointEmitterTool implements SceneTool {
	private Flock flock;

	public PointEmitterTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void render(PGraphics graphics) {

	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		flock.addEmitter(new PointEmitter(mouseX, mouseY, 1));
	}

	@Override
	public void keyDown(char key) {

	}

	@Override
	public String toString() {
		return "POINT EM";
	}
}
