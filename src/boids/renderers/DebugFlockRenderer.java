package boids.renderers;

import boids.Flock;
import boids.behaviors.Behavior;
import boids.behaviors.FollowPathBehavior;
import boids.behaviors.MagnetBehavior;
import boids.emitter.Emitter;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;

public class DebugFlockRenderer extends FlockRenderer {
	public DebugFlockRenderer(Flock flock, PGraphics canvas) {
		super(flock, canvas);
	}

	public void render() {
		canvas.stroke(0, 255, 0);
		canvas.noFill();
		canvas.strokeWeight(2);
		for (Emitter e : flock.getEmitters()) {
			if (e instanceof LineEmitter) {
				LineEmitter le = (LineEmitter) e;
				canvas.line(le.getP1().x, le.getP1().y, le.getP2().x, le.getP2().y);
			} else if (e instanceof PointEmitter) {
				PointEmitter pe = (PointEmitter) e;
				canvas.pushMatrix();
				canvas.translate(pe.getPosition().x, pe.getPosition().y);
				canvas.line(-10, 0, 10, 0);
				canvas.line(0, -10, 0, 10);
				canvas.popMatrix();
			}
		}

		for (Behavior behavior : flock.getBehaviors()) {
			if (behavior instanceof FollowPathBehavior) {
				renderFollowPathBehavior(canvas, (FollowPathBehavior) behavior);
			} else if (behavior instanceof MagnetBehavior) {
				renderMagnetBehavior(canvas, (MagnetBehavior) behavior);
			}
		}

		canvas.stroke(255);
		canvas.strokeWeight(2);
		Rectangle bounds = flock.getBounds();
		canvas.noFill();
		canvas.rect(bounds.x, bounds.y, bounds.width, bounds.height);

		canvas.stroke(255);
		canvas.textSize(24);
		canvas.text(flock.getBoids().size(), 100, 100);
	}

	private void renderFollowPathBehavior(PGraphics canvas, FollowPathBehavior behavior) {
		canvas.noFill();
		canvas.stroke(20);
		canvas.strokeCap(PConstants.ROUND);
		canvas.strokeWeight(behavior.getRadius() * 2);
		canvas.beginShape();
		for (PVector p : behavior.getPoints()) {
			canvas.vertex(p.x, p.y, -1);
		}
		canvas.endShape();
	}

	private void renderMagnetBehavior(PGraphics canvas, MagnetBehavior behavior) {
		canvas.noStroke();
		canvas.fill(255, 0, 0);
		for (PVector m : behavior.getMagnets()) {
			canvas.ellipse(m.x, m.y, 10, 10);
		}
	}
}
