package scenes;

import boids.*;
import boids.behaviors.FlockBehavior;
import boids.behaviors.FollowPathBehavior;
import boids.behaviors.MagnetBehavior;
import boids.behaviors.WiggleBehavior;
import boids.renderers.BoidRenderer;
import boids.renderers.WormBoidRenderer;
import emitter.Emitter;
import emitter.LineEmitter;
import emitter.PointEmitter;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Worms extends Scene implements FlockEventListener {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private Flock flock;
	private MagnetBehavior magnets;
	private FollowPathBehavior path;
	private List<BoidRenderer> renderers;

	@Override
	public void doSetup() {
		renderers = new ArrayList<>();

		flock = new Flock(100, 100, WIDTH - 200, HEIGHT - 200);
		flock.addEventListener(this);

		FlockBehavior flockingBehavior = new FlockBehavior();
		flockingBehavior.setSeparationWeight(2f);
		flockingBehavior.setDesiredSeparation(50f);
		flockingBehavior.setNeighborDistance(100f);
		flock.addBehavior(flockingBehavior);

		WiggleBehavior wiggleBehavior = new WiggleBehavior(1, 10);
		flock.addBehavior(wiggleBehavior);

		magnets = new MagnetBehavior(-10, 300, 5, 5);
		flock.addBehavior(magnets);

		path = new FollowPathBehavior(new PVector(300, 300), new PVector(800, 400), 50);
		flock.addBehavior(path);

		LineEmitter e1 = new LineEmitter(100, 100, WIDTH - 100, 100, 0.5f);
		e1.setInitialVelocity(new PVector(0, 2));
		flock.addEmitter(e1);

		LineEmitter e2 = new LineEmitter(100, HEIGHT - 101, WIDTH - 100, HEIGHT - 101, 0.5f);
		e2.setInitialVelocity(new PVector(0, -2));
		flock.addEmitter(e2);
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		flock.step(elapsedMillis);

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

		canvas.stroke(255);
		canvas.strokeWeight(3);
		canvas.strokeCap(ROUND);

		Iterator<BoidRenderer> rendererIterator = renderers.iterator();
		while (rendererIterator.hasNext()) {
			BoidRenderer renderer = rendererIterator.next();
			if (renderer.isReadyToDie()) {
				rendererIterator.remove();
			}
			renderer.draw(canvas);
		}

		canvas.noStroke();
		canvas.fill(255, 0, 0);
		for (PVector m : magnets.getMagnets()) {
			canvas.ellipse(m.x, m.y, 10, 10);
		}

//		canvas.stroke(255, 100);
//		canvas.strokeWeight(path.getRadius() * 2);
//		canvas.line(path.getStart().x, path.getStart().y, path.getEnd().x, path.getEnd().y);
//
//		canvas.stroke(255);
//		canvas.strokeWeight(2);
//		canvas.line(path.getStart().x, path.getStart().y, path.getEnd().x, path.getEnd().y);

		canvas.stroke(255);
		canvas.strokeWeight(2);
		Rectangle bounds = flock.getBounds();
		canvas.noFill();
		canvas.rect(bounds.x, bounds.y, bounds.width, bounds.height);

		canvas.stroke(255);
		canvas.textSize(24);
		canvas.text(flock.getBoids().size(), 100, 100);
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		magnets.addMagnet(mouseX, mouseY);
	}

	@Override
	public void boidAdded(Boid b) {
		BoidRenderer renderer = new WormBoidRenderer(b, 10);
		b.setUserData("renderer", renderer);
		renderers.add(renderer);
	}

	@Override
	public void boidRemoved(Boid b) {
		BoidRenderer boidRenderer = (BoidRenderer) b.getUserData("renderer");
		boidRenderer.markReadyForDeath();
	}

	public void keyPressed() {
		magnets.setRepelling(!magnets.isRepelling());
	}
}
