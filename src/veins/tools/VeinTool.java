package veins.tools;

import processing.core.PVector;
import sketches.SceneTool;
import veins.Tree;

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
