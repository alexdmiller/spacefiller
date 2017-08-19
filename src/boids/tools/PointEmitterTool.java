package boids.tools;

import boids.Flock;
import boids.emitter.PointEmitter;
import particles.Bounds;
import sketches.SceneTool;

public class PointEmitterTool extends SceneTool {
	private static final char DECREASE_CHANCE_KEY = '[';
	private static final char INCREASE_CHANCE_KEY = ']';
	private static final char TOGGLE_TEAM_KEY = 't';
	private static final float CHANCE_INCREMENT = 0.5f;

	private Flock flock;
	private float chance = 1;
	private int team;

	public PointEmitterTool(Flock emitBehavior) {
		this.flock = emitBehavior;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		Bounds b = flock.getBounds();
		flock.addEmitter(new PointEmitter(mouseX, mouseY, 0, chance, team));
	}

	@Override
	public void keyDown(char key) {
		if (key == INCREASE_CHANCE_KEY) {
			chance += CHANCE_INCREMENT;
		} else if (key == DECREASE_CHANCE_KEY) {
			chance -= CHANCE_INCREMENT;
		} else if (key == TOGGLE_TEAM_KEY) {
			team = (team + 1) % 2;
		}
	}

	@Override
	public String toString() {
		return "POINT (chance " + chance + ", team " + team + ")";
	}

	@Override
	public void clear() {
		flock.clearEmitters();
	}
}
