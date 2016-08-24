package boids.tools;

import boids.behaviors.MagnetBehavior;
import processing.core.PGraphics;
import scenes.SceneTool;

public class MagnetTool implements SceneTool {
	private static final char DECREASE_STRENGTH_KEY = '<';
	private static final char INCREASE_STRENGTH_KEY = '>';
	private static final float STRENGTH_INCREMENT = 0.5f;

	private MagnetBehavior magnets;
	private float strength = 1;

	public MagnetTool(MagnetBehavior magnets) {
		this.magnets = magnets;
	}

	@Override
	public void render(PGraphics graphics) {

	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		magnets.addMagnet(mouseX, mouseY, strength);
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
	public String toString() {
		return "MAGNET - strength " + strength;
	}

}
