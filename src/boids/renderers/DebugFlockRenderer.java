package boids.renderers;

import boids.Flock;
import boids.Magnet;
import boids.behaviors.Behavior;
import boids.behaviors.EmitBehavior;
import boids.behaviors.FollowPathBehavior;
import boids.behaviors.MagnetBehavior;
import boids.emitter.Emitter;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.util.List;

public class DebugFlockRenderer extends FlockRenderer {
	public DebugFlockRenderer(Flock flock) {
		super(flock);
	}

	public void render(PGraphics graphics) {
		renderFollowPathBehavior(graphics, flock.getPathPoints());
		renderMagnetBehavior(graphics, flock.getMagnets());
		renderEmitBehavior(graphics, flock.getEmitters());

		graphics.stroke(255);
		graphics.strokeWeight(2);
		Rectangle bounds = flock.getBounds();
		graphics.noFill();
		graphics.rect(bounds.x, bounds.y, bounds.width, bounds.height);

		graphics.stroke(255);
		graphics.textSize(24);
		//graphics.text(flock.getBoids().size(), 100, 100);
	}

	private void renderEmitBehavior(PGraphics graphics, List<Emitter> emitters) {
		graphics.stroke(0, 255, 0);
		graphics.noFill();
		graphics.strokeWeight(2);
		for (Emitter e : emitters) {
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
	}

	private void renderFollowPathBehavior(PGraphics canvas, List<PVector> pathPoints) {
		canvas.noFill();
		canvas.stroke(255);
		canvas.strokeCap(PConstants.ROUND);
		canvas.strokeWeight(5);
		canvas.beginShape();
		for (PVector p : pathPoints) {
			canvas.vertex(p.x, p.y);
		}
		canvas.endShape();
	}

	private void renderMagnetBehavior(PGraphics canvas, List<Magnet> magnets) {
		canvas.noStroke();
		canvas.fill(255, 0, 0);
		for (Magnet m : magnets) {
			canvas.ellipse(m.position.x, m.position.y, 10, 10);
		}
	}
}
