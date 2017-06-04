package boids.renderers;

import boids.Boid;
import boids.Flock;
import megamu.mesh.Delaunay;
import modulation.Mod;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.List;

public class MeshLikeFlockRenderer extends FlockRenderer {
	@Mod(min = 0, max = 1000, defaultValue = 100)
	public float lineThreshold = 100;

	public MeshLikeFlockRenderer(Flock flock) {
		super(flock);
	}

	@Override
	public void render(PGraphics graphics) {
		graphics.rotateY(flock.getRotation());

		List<Boid> boids = flock.getBoids();
		for (int i = 0; i < boids.size(); i++) {
			Boid b1 = boids.get(i);

			for (int j = i + 1; j < boids.size(); j++) {
				Boid b2 = boids.get(j);

				float dist = PVector.dist(b1.getPosition(), b2.getPosition());
				if (dist < lineThreshold) {
					graphics.line(b1.getPosition().x, b1.getPosition().y, b1.getPosition().z, b2.getPosition().x, b2.getPosition().y, b2.getPosition().z);
				}
			}
		}
	}
}
