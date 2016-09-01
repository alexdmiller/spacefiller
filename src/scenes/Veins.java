package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import processing.core.PGraphics;
import processing.core.PVector;
import veins.Tree;
import veins.tools.FoodTool;
import veins.tools.VeinTool;

public class Veins extends Scene {
	public static void main(String[] args) {
		main("scenes.Veins");
	}

	private List<PVector> attractors;
	private Tree tree;

	@Mod(min=0.1f, max=50)
	float growthSpeed = 5;

	@Mod(min=10, max=200)
	float attractorKillRadius = 10;

	@Mod(min=10, max=1000)
	float attractorInfluenceRadius = 100;

	@Mod(min=0, max=20)
	float edgeThickness = 5;

	@Mod(min=0, max=20)
	float pulsePeriod = 50;

	@Mod(min=0, max=1000)
	float pulseLife = 200;

	@Override
	public void doSetup() {
		attractors = new ArrayList<PVector>();
		tree = new Tree();

		addSceneTool(new FoodTool(tree, attractors, 10, 100));
		addSceneTool(new VeinTool(tree));
	}

	@Override
	protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
		graphics.background(0);

		tree.grow(attractors, attractorInfluenceRadius, attractorKillRadius, growthSpeed);

		drawTree(tree, graphics);
	}

	@Override
	protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {
		drawAttractors(attractors, graphics);
	}

	void drawTree(Tree tree, PGraphics graphics) {
		graphics.stroke(255);

		Iterator<Tree.Edge> edges = tree.edges.iterator();
		while (edges.hasNext()) {
			Tree.Edge edge = edges.next();
			if (edge.age >= pulseLife) {
				edges.remove();
			} else {
				float w = edgeThickness * ageToThickness(edge.age);
				if (w > 0) {
					graphics.strokeWeight(w);
					graphics.line(edge.n1.v.x, edge.n1.v.y, edge.n2.v.x, edge.n2.v.y);
				}
				edge.age++;
			}
		}

		Iterator<Tree.Node> nodes = tree.nodes.iterator();
		while (nodes.hasNext()) {
			Tree.Node node = nodes.next();
			if (node.age >= pulseLife) {
				nodes.remove();
			} else {
				node.age++;
			}
		}
	}

	void drawAttractors(List<PVector> attractors, PGraphics graphics) {
		graphics.strokeWeight(5);
		for (PVector attractor : attractors) {
			graphics.stroke(255);
			graphics.point(attractor.x, attractor.y);
		}
	}

	float ageToThickness(int age) {
		return Math.max(0, sin((float) age * (PI / pulsePeriod)));
	}
}
