package boids.tools;

import boids.Flock;
import boids.emitter.PointEmitter;
import scenes.SceneTool;

public class PointEmitterTool extends SceneTool {
	private static final char DECREASE_CHANCE_KEY = '[';
	private static final char INCREASE_CHANCE_KEY = ']';
	private static final float CHANCE_INCREMENT = 0.5f;

	private Flock flock;
	private float chance = 1;

	public PointEmitterTool(Flock emitBehavior) {
		this.flock = emitBehavior;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		flock.addEmitter(new PointEmitter(mouseX, mouseY, chance));
	}

	@Override
	public void keyDown(char key) {
		if (key == INCREASE_CHANCE_KEY) {
			chance += CHANCE_INCREMENT;
		} else if (key == DECREASE_CHANCE_KEY) {
			chance -= CHANCE_INCREMENT;
		}
	}

	@Override
	public String toString() {
		return "POINT (chance " + chance + ")";
	}

	@Override
	public void clear() {
		flock.clearEmitters();
	}
}
