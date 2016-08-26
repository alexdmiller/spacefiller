package boids.tools;

import boids.Flock;
import boids.behaviors.FollowPathBehavior;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PGraphics;
import processing.core.PVector;
import scenes.SceneTool;

public class PathTool extends SceneTool {
	private FollowPathBehavior path;

	public PathTool(FollowPathBehavior path) {
		this.path = path;
	}

	@Override
	public void mousePressed(float mouseX, float mouseY) {
		path.addPoint(mouseX, mouseY);
	}

	@Override
	public String toString() {
		return "PATH";
	}

}
