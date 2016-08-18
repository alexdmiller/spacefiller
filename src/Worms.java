import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PVector;

public class Worms extends Scene {
	public static void main(String[] args) {
		PApplet.main("Worms");
	}

	private Flock flock;
	private BoidRenderer renderer;

	@Override
	public void doSetup() {
		renderer = new PointBoidRenderer();

		flock = new Flock();

		for (int i = 0; i < 100; i++) {
			flock.addBoid(new Boid(500, 500));
		}
	}

	@Override
	protected void doDraw(float mouseX, float mouseY) {
		canvas.background(0);
		flock.run();

		canvas.stroke(255);
		canvas.strokeWeight(5);
		for (Boid b : flock.boids) {
			renderer.draw(b, canvas);
		}
	}

	@Override
	protected void doMousePressed(float mouseX, float mouseY) {
	}
}
