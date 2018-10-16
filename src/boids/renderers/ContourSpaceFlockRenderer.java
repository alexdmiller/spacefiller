package boids.renderers;

import boids.Boid;
import boids.Flock;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.ContourSpace;
import spacefiller.LineSegment;
import spacefiller.Vector;
import spacefiller.remote.Mod;

import java.util.List;

public class ContourSpaceFlockRenderer extends FlockRenderer {
  private ContourSpace contourSpace;
  private int width, height;

  @Mod(min = 1, max = 50)
  public float boidRadius = 10;

  @Mod(min = 0.2f, max = 3)
  public float maxThreshold = 2;

  public ContourSpaceFlockRenderer(Flock flock, int width, int height, float cellSize) {
    super(flock);

    width += cellSize * 2;
    height += cellSize * 2;

    contourSpace = new ContourSpace(width, height, cellSize);
    this.width = width;
    this.height = height;
  }

  @Mod(min = 20, max = 200)
  public void setCellSize(float cellSize) {
    contourSpace = new ContourSpace(width, height, cellSize);
  }

  @Override
  public void render(PGraphics graphics) {
    contourSpace.resetGrid();

    List<Boid> boids = flock.getBoids();
    PVector offset = new PVector(width / 2, height / 2);
    for (Boid b : boids) {
      PVector v = PVector.add(b.getPosition(), offset);
      contourSpace.addMetaBall(new Vector(v.x, v.y), boidRadius, 1);
    }

    contourSpace.clearLineSegments();

    float step = 0.02f;
    float threshold = 0.2f;

    while (threshold < maxThreshold) {
      contourSpace.drawIsoContour(threshold);
      threshold += step;
      step *= 1.5;
    }

    graphics.translate(-width / 2, -height / 2);
    graphics.stroke(255);
    graphics.noFill();

    for (List<LineSegment> layer : contourSpace.getLayers()) {
      for (LineSegment lineSegment : layer) {
        graphics.line(lineSegment.p1.x, lineSegment.p1.y, lineSegment.p2.x, lineSegment.p2.y);
      }
    }
  }
}
