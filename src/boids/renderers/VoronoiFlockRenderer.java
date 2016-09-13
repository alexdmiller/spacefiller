package boids.renderers;

import boids.Boid;
import boids.Flock;
import boids.BoidEventListener;
import boids.behaviors.Behavior;
import megamu.mesh.Delaunay;
import megamu.mesh.Voronoi;
import processing.core.PGraphics;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VoronoiFlockRenderer extends FlockRenderer {
	public VoronoiFlockRenderer(Flock flock) {
		super(flock);
	}

	@Override
	public void render(PGraphics graphics) {
		List<Boid> boids = flock.getBoids();
		float[][] points = new float[boids.size()][2];
		for (int i = 0; i < boids.size(); i++) {
			points[i][0] = boids.get(i).getPosition().x;
			points[i][1] = boids.get(i).getPosition().y;
		}
		try {
			Voronoi voronoi = new Voronoi(points);
			float[][] myEdges = voronoi.getEdges();
			for (int i = 0; i < myEdges.length; i++) {
				float startX = myEdges[i][0];
				float startY = myEdges[i][1];
				float endX = myEdges[i][2];
				float endY = myEdges[i][3];
				graphics.line(startX, startY, endX, endY);
			}
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}
}
