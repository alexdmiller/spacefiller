package veins.tools;

import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;
import veins.Tree;

import java.util.List;

public class VeinTool extends SceneTool {
	private Tree tree;


	public VeinTool(Tree tree) {
		this.tree = tree;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		tree.addNode(new PVector(mouseX, mouseY));
	}

	@Override
	public String toString() {
		return "VEIN";
	}
}
