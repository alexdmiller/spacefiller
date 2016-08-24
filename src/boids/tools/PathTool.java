package boids.tools;

import boids.Flock;
import boids.behaviors.FollowPathBehavior;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;

public class PathTool implements SceneTool {
	private FollowPathBehavior path;

	public PathTool(FollowPathBehavior path) {
		this.path = path;
	}

	@Override
	public void render(PGraphics graphics) {

	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		path.addPoint(mouseX, mouseY);
	}

	@Override
	public void keyDown(char key) {

	}

	@Override
	public String toString() {
		return "PATH";
	}

}
