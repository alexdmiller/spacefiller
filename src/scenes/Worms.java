package scenes;

import boids.*;
import boids.behaviors.FlockBehavior;
import boids.renderers.BoidRenderer;
import boids.renderers.PointBoidRenderer;
import boids.renderers.WormBoidRenderer;
import emitter.Emitter;
import emitter.LineEmitter;
import emitter.PointEmitter;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class Worms extends Scene implements FlockEventListener {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private Flock flock;
	private List<BoidRenderer> renderers;

	@Override
	public void doSetup() {
		renderers = new ArrayList<>();

		flock = new Flock();
		flock.addEventListener(this);

		FlockBehavior flockingBehavior = new FlockBehavior();
		flockingBehavior.setSeparationWeight(2f);
		flockingBehavior.setDesiredSeparation(100f);
		flockingBehavior.setNeighborDistance(200f);
		flock.addBehavior(flockingBehavior);

		LineEmitter e = new LineEmitter(0, 0, WIDTH, 0, 0.5f);
		e.setInitialVelocity(new PVector(0, 2));
		flock.addEmitter(e);

		canvas.noSmooth();
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		flock.step(elapsedMillis);

		canvas.stroke(0, 255, 0);
		canvas.noStroke();
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
		canvas.strokeWeight(5);
		canvas.strokeCap(ROUND);
		for (BoidRenderer renderer : renderers) {
			renderer.draw(canvas);
		}

		canvas.noStroke();
		canvas.fill(255, 0, 0);
		for (Magnet m : flock.getMagnets()) {
			canvas.ellipse(m.position.x, m.position.y, 10, 10);
		}

		canvas.stroke(255);
		canvas.textSize(24);
		canvas.text(flock.getBoids().size(), 100, 100);
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
		flock.addMagnet(new Magnet(mouseX, mouseY, -20, 200));
	}

	@Override
	public void boidAdded(Boid b) {
		renderers.add(new WormBoidRenderer(b, 20));
	}

	@Override
	public void boidRemoved(Boid b) {

	}
}
