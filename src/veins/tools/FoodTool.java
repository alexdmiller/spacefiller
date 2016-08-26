package veins.tools;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;
import veins.Tree;

import java.util.List;

/**
 * Created by miller on 8/24/16.
 */
public class FoodTool extends SceneTool {
	private Tree tree;
	private List<PVector> attractors;
	private float brushDensity;
	private float brushRadius;

	public FoodTool(Tree tree, List<PVector> attractors, float brushDensity, float brushRadius) {
		this.tree = tree;
		this.attractors = attractors;
		this.brushDensity = brushDensity;
		this.brushRadius = brushRadius;
	}

	@Override
	public void render(PGraphics graphics, float mouseX, float mouseY, boolean mousePressed) {
		if (mousePressed) {
			for (int i = 0; i < brushDensity; i++) {
				attractors.add(new PVector(
						(float) (mouseX + Math.random() * brushRadius * 2 - brushRadius),
						(float) (mouseY + Math.random() * brushRadius * 2 - brushRadius)));
			}
		}

		graphics.stroke(255);
		graphics.noFill();
		graphics.rectMode(PConstants.RADIUS);
		graphics.rect(mouseX, mouseY, brushRadius, brushRadius);
	}

	@Override
	public String toString() {
		return "FOOD";
	}
}
