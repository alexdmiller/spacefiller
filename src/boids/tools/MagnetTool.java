package boids.tools;

import boids.Flock;
import boids.Magnet;
import processing.core.PGraphics;
import processing.core.PVector;
import sketches.SceneTool;

public class MagnetTool extends SceneTool {
	private static final char DECREASE_STRENGTH_KEY = '[';
	private static final char INCREASE_STRENGTH_KEY = ']';
	private static final float STRENGTH_INCREMENT = 0.5f;

	private Flock flock;
	private float strength = 1;
	private Magnet currentMagnet;

	public MagnetTool(Flock flock) {
		this.flock = flock;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		currentMagnet = new Magnet(new PVector(mouseX, mouseY), 0, strength);
	}

	@Override
	public void mouseReleased(float mouseX, float mouseY) {
		flock.addMagnet(currentMagnet);
		currentMagnet = null;
	}

	@Override
	public void mouseDragged(float mouseX, float mouseY) {
		currentMagnet.radius = PVector.dist(currentMagnet.position, new PVector(mouseX, mouseY));
	}

	@Override
	public void keyDown(char key) {
		if (key == INCREASE_STRENGTH_KEY) {
			strength += STRENGTH_INCREMENT;
		} else if (key == DECREASE_STRENGTH_KEY) {
			strength -= STRENGTH_INCREMENT;
		}
	}

	@Override
	public void render(PGraphics graphics, float mouseX, float mouseY, boolean mousePressed) {
		if (currentMagnet != null) {
			graphics.stroke(255);
			graphics.noFill();
			graphics.ellipse(currentMagnet.position.x, currentMagnet.position.y, currentMagnet.radius * 2, currentMagnet.radius * 2);
		}
	}

	@Override
	public String toString() {
		return "MAGNET (strength " + strength + ")";
	}

	@Override
	public void clear() {
		flock.clearMagnets();
	}
}
