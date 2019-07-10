package boids.renderers;

import boids.Boid;
import boids.Flock;
import boids.BoidEventListener;
import boids.behaviors.Behavior;
import spacefiller.color.SmoothColorTheme;
import megamu.mesh.Delaunay;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.color.ColorRange;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MeshFlockRenderer extends FlockRenderer {
	SmoothColorTheme theme;

	public MeshFlockRenderer(Flock flock) {
		super(flock);
		theme = new SmoothColorTheme(ColorRange.BRIGHT, 10, 100);
	}

	@Override
	public void render(PGraphics graphics) {
		List<Boid> boids = flock.getBoids();
		float[][] points = new float[boids.size()][2];
		for (int i = 0; i < boids.size(); i++) {
			points[i][0] = boids.get(i).getPosition().x;
			points[i][1] = boids.get(i).getPosition().y;
		}
		Delaunay delaunay = new Delaunay(points);
		float[][] myEdges = delaunay.getEdges();
		for(int i=0; i<myEdges.length; i++) {
			float startX = myEdges[i][0];
			float startY = myEdges[i][1];
			float endX = myEdges[i][2];
			float endY = myEdges[i][3];

			float dx = startX - endX;
			float dy = startY - startY;
			float dist = dx * dx + dy * dy;

			graphics.stroke(theme.getColor(dist / 5000).toARGB());
			graphics.line( startX, startY, endX, endY );
		}
	}
}
