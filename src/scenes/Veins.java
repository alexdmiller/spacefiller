package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import processing.core.PVector;

public class Veins extends Scene {
	public static void main(String[] args) {
		main("scenes.Veins");
	}

	final static int BRUSH_DENSITY = 5;
	final static float BRUSH_RADIUS = 100;

	List<PVector> attractors;
	Tree tree;
	boolean drawing = true;

	@Scene.ModulationTarget(min=0.1f, max=50)
	float growthSpeed = 5;

	@Scene.ModulationTarget(min=10, max=200)
	float attractorKillRadius = 10;

	@Scene.ModulationTarget(min=10, max=1000)
	float attractorInfluenceRadius = 100;

	@Scene.ModulationTarget(min=0, max=20)
	float edgeThickness = 5;

	@Scene.ModulationTarget(min=0, max=20)
	float pulsePeriod = 2;

	@Scene.ModulationTarget(min=0, max=1000)
	float pulseLife = 200;

	@Override
	public void doSetup() {
		attractors = new ArrayList<PVector>();
		tree = new Tree();
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		if (drawing) {
			drawAttractors(attractors);

			if (mousePressed) {
				for (int i = 0; i < BRUSH_DENSITY; i++) {
					attractors.add(new PVector(
							mouseX + random(-BRUSH_RADIUS, BRUSH_RADIUS),
							mouseY + random(-BRUSH_RADIUS, BRUSH_RADIUS)));
				}
			}

			canvas.stroke(255);
			canvas.noFill();
			canvas.rectMode(RADIUS);
			canvas.rect(mouseX, mouseY, BRUSH_RADIUS, BRUSH_RADIUS);
		}

		tree.grow(attractors);

		drawTree(tree);
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		if (!drawing) {
			tree.addNode(new PVector(mouseX, mouseY));
		}
	}

	public void keyPressed() {
		drawing = !drawing;
	}

	void drawTree(Tree tree) {
		canvas.stroke(255);

		Iterator<Edge> edges = tree.edges.iterator();
		while (edges.hasNext()) {
			Edge edge = edges.next();
			if (edge.age >= pulseLife) {
				edges.remove();
			} else {
				float w = edgeThickness * ageToThickness(edge.age);
				if (w > 0) {
					canvas.strokeWeight(w);
					canvas.line(edge.n1.v.x, edge.n1.v.y, edge.n2.v.x, edge.n2.v.y);
				}
				edge.age++;
			}
		}

		Iterator<Node> nodes = tree.nodes.iterator();
		while (nodes.hasNext()) {
			Node node = nodes.next();
			if (node.age >= pulseLife) {
				nodes.remove();
			} else {
				node.age++;
			}
		}
	}

	void drawAttractors(List<PVector> attractors) {
		canvas.strokeWeight(5);
		for (PVector attractor : attractors) {
			canvas.stroke(255);
			canvas.point(attractor.x, attractor.y);
		}
	}

	float ageToThickness(int age) {
		return Math.max(0, sin((float) age * (PI / pulsePeriod)));
	}


	class Tree {
		List<Edge> edges;
		List<Node> nodes;

		Tree() {
			edges = new ArrayList<Edge>();
			nodes = new ArrayList<Node>();
		}

		void grow(List<PVector> attractors) {
			Map<Node, PVector> forces = new HashMap<Node, PVector>();
			List<PVector> attractorsToRemove = new ArrayList<PVector>();

			for (PVector attractor : attractors) {

				// Find the closest node to attractor.
				Node closest = null;
				for (Node node : nodes) {
					float dist = attractor.dist(node.v);
					if (dist < attractorInfluenceRadius &&
							(closest == null ||
									dist < attractor.dist(closest.v))) {
						closest = node;
					}

					if (dist < attractorKillRadius) {
						attractorsToRemove.add(attractor);
					}
				}

				// Apply a force to the nearest node.
				if (closest != null) {
					if (!forces.containsKey(closest)) {
						forces.put(closest, new PVector(0, 0));
					}
					PVector diff = new PVector();
					diff.set(attractor);
					diff.sub(closest.v);
					diff.normalize();
					forces.get(closest).add(diff);
				}
			}

			attractors.removeAll(attractorsToRemove);

			List<Node> newNodes = new ArrayList<Node>();
			for (Node node : nodes) {
				if (forces.containsKey(node)) {
					PVector force = forces.get(node);
					force.normalize();
					force.mult(growthSpeed + random(-1, 1));
					force.add(node.v);

					Node n = new Node(force);

					newNodes.add(n);
					edges.add(new Edge(node, n));
				}
			}

			nodes.addAll(newNodes);
		}

		void addNode(PVector n) {
			nodes.add(new Node(n));
		}
	}

	class Node {
		PVector v;
		int age;

		Node(PVector v) {
			this.v = v;
		}

		public String toString() {
			return this.v.toString() + " " + String.valueOf(this.v.hashCode());
		}
	}

	class Edge {
		Node n1, n2;
		int age;

		Edge(Node n1, Node n2) {
			this.n1 = n1;
			this.n2 = n2;
		}
	}
}
