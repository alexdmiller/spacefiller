package boids.renderers;

import boids.Flock;
import boids.Magnet;
import boids.PathSegment;
import boids.emitter.Emitter;
import boids.emitter.LineEmitter;
import boids.emitter.PointEmitter;
import particles.Bounds;
import common.StoredVectorField;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.List;

public class DebugFlockRenderer extends FlockRenderer {
	public DebugFlockRenderer(Flock flock) {
		super(flock);
	}

	public void render(PGraphics graphics) {
		Bounds bounds = flock.getBounds();


		renderFollowPathBehavior(graphics, flock.getPathSegments());
		renderMagnetBehavior(graphics, flock.getMagnets());
		renderEmitBehavior(graphics, flock.getEmitters());
		renderFlowField(graphics, flock);

		graphics.stroke(255);
		graphics.strokeWeight(2);
		graphics.noFill();
		graphics.box(bounds.getWidth(), bounds.getHeight(), bounds.getDepth());


		graphics.stroke(255);
		graphics.textSize(24);
		graphics.text(flock.getBoids().size(), 100, 100);
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
				graphics.sphere(10);
				graphics.popMatrix();
			}
		}
	}

	private void renderFollowPathBehavior(PGraphics canvas, List<PathSegment> pathPoints) {
		canvas.noFill();
		canvas.stroke(255);
		canvas.strokeCap(PConstants.ROUND);
		canvas.strokeWeight(5);
		for (PathSegment pair : pathPoints) {
			PVector p1 = pair.p1;
			PVector p2 = pair.p2;
			canvas.line(p1.x, p1.y, p2.x, p2.y);
		}
	}

	private void renderMagnetBehavior(PGraphics canvas, List<Magnet> magnets) {
		for (Magnet m : magnets) {
			canvas.noStroke();
			canvas.fill(255, 0, 0);
			canvas.ellipse(m.position.x, m.position.y, 10, 10);

			canvas.noFill();
			canvas.stroke(255, 0, 0);
			canvas.ellipse(m.position.x, m.position.y, m.radius * 2, m.radius * 2);
		}
	}

	private void renderFlowField(PGraphics canvas, Flock flock) {
		canvas.stroke(255);

		StoredVectorField field = (StoredVectorField) flock.getFlowField();
		Bounds bounds = flock.getBounds();

		for (int x = 0; x < flock.getFlowFieldWidth(); x++) {
			for (int y = 0; y < flock.getFlowFieldHeight(); y++) {
				for (int z = 0; z < flock.getFlowFieldDepth(); z++) {
					PVector v = field.getCell(x, y, z);
					canvas.pushMatrix();
					float posX = x * field.getCellSize() + field.getCellSize() / 2 - bounds.getWidth() / 2;
					float posY = y * field.getCellSize() + field.getCellSize() / 2 - bounds.getHeight() / 2;
					float posZ = z * field.getCellSize() + field.getCellSize() / 2 - bounds.getDepth() / 2;

					v = PVector.div(v, 5);
					canvas.translate(posX, posY, posZ);
					canvas.line(0, 0, 0, v.x, v.y, v.z);
					canvas.popMatrix();
				}
			}
		}
	}
}
