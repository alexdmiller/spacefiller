package boids.renderers;

import boids.Boid;
import boids.Flock;
import particles.Bounds;
import processing.core.PGraphics;

import java.util.List;

public class GravityFlockRenderer extends FlockRenderer {
	public GravityFlockRenderer(Flock flock) {
		super(flock);
	}

	@Override
	public void render(PGraphics graphics) {
		List<Boid> boids = flock.getBoids();

		Bounds bounds = flock.getBounds();
		for (float i = 0; i < bounds.getWidth(); i += 100) {
			for (float j = 0; j < bounds.getWidth(); j += 100) {
				float x = i;
				float y = j;
				float vx = 0;
				float vy = 0;

				for (int steps = 0; steps < 10; steps++) {
					for (Boid b : boids) {
						float dx = b.getPosition().x - x;
						float dy = b.getPosition().y - y;
						float dist = (float) Math.sqrt(dx * dx + dy * dy);
						float angle = (float) Math.atan2(dy, dx);
						float force = 100 / (dist * dist);
						force = Math.min(force, 1);

						vx += Math.cos(angle) * force;
						vy += Math.sin(angle) * force;

						float newX = x + vx;
						float newY = y + vy;

						graphics.line(x, y, newX, newY);

						x = newX;
						y = newY;
					}
				}
			}
		}
	}
}
