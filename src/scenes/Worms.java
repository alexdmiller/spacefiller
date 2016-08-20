package scenes;

import boids.*;
import emitter.Emitter;
import emitter.LineEmitter;
import emitter.PointEmitter;

public class Worms extends Scene {
	public static void main(String[] args) {
		main("scenes.Worms");
	}

	private Flock flock;
	private BoidRenderer renderer;

	@Override
	public void doSetup() {
		renderer = new PointBoidRenderer();
		flock = new Flock();

		flock.addEmitter(new LineEmitter(100, HEIGHT / 2, WIDTH - 100, HEIGHT / 2, 0.5f));
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);

		flock.step(elapsedMillis);

		canvas.stroke(0, 255, 0);
		canvas.noFill();
		canvas.strokeWeight(5);
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
		canvas.strokeWeight(10);
		for (Boid b : flock.getBoids()) {
			renderer.draw(b, canvas);
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
		flock.addMagnet(new Magnet(mouseX, mouseY, -20, 500));
	}
}
