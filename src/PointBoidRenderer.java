import processing.core.PGraphics;

public class PointBoidRenderer implements BoidRenderer {
	@Override
	public void draw(Boid boid, PGraphics graphics) {
		graphics.point(boid.location.x, boid.location.y);
	}
}
