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
	public DebugFlockRenderer(Flock flock) {
		super(flock);
	}

	public void render(PGraphics graphics) {
		graphics.stroke(0, 255, 0);
		graphics.noFill();
		graphics.strokeWeight(2);
		for (Emitter e : flock.getEmitters()) {
			if (e instanceof LineEmitter) {
				LineEmitter le = (LineEmitter) e;
				graphics.line(le.getP1().x, le.getP1().y, le.getP2().x, le.getP2().y);
			} else if (e instanceof PointEmitter) {
				PointEmitter pe = (PointEmitter) e;
				graphics.pushMatrix();
				graphics.translate(pe.getPosition().x, pe.getPosition().y);
				graphics.line(-10, 0, 10, 0);
				graphics.line(0, -10, 0, 10);
				graphics.popMatrix();
			}
		}

		for (Behavior behavior : flock.getBehaviors()) {
			if (behavior instanceof FollowPathBehavior) {
				renderFollowPathBehavior(graphics, (FollowPathBehavior) behavior);
			} else if (behavior instanceof MagnetBehavior) {
				renderMagnetBehavior(graphics, (MagnetBehavior) behavior);
			}
		}

		graphics.stroke(255);
		graphics.strokeWeight(2);
		Rectangle bounds = flock.getBounds();
		graphics.noFill();
		graphics.rect(bounds.x, bounds.y, bounds.width, bounds.height);

		graphics.stroke(255);
		graphics.textSize(24);
		//graphics.text(flock.getBoids().size(), 100, 100);
	}

	private void renderFollowPathBehavior(PGraphics canvas, FollowPathBehavior behavior) {
		canvas.noFill();
		canvas.stroke(20);
		canvas.strokeCap(PConstants.ROUND);
		canvas.strokeWeight(behavior.getRadius() * 2);
		canvas.beginShape();
		for (PVector p : behavior.getPoints()) {
			canvas.vertex(p.x, p.y);
		}
		canvas.endShape();
	}

	private void renderMagnetBehavior(PGraphics canvas, MagnetBehavior behavior) {
		canvas.noStroke();
		canvas.fill(255, 0, 0);
		for (MagnetBehavior.Magnet m : behavior.getMagnets()) {
			canvas.ellipse(m.position.x, m.position.y, 10, 10);
		}
	}
}
