package lusio.generators;

import lusio.Lusio;
import modulation.Mod;
import particles.Bounds;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by miller on 7/22/17.
 */
public class ContourGenerator extends SceneGenerator {
  private float noiseAmplitude = 0;

  private float updateSpeed = 0.01f;
  private float xSpeed = 0f;
  private float ySpeed = 0f;

  private float cellSize = 20;
  private float heightIncrements = 2;
  private float heightRange = 100;
  private float spacing = 4;
  private int colorRange = 50;

  private float timeStep = 0;
  private float xTimeStep = 0;
  private float yTimeStep = 0;

  private Bounds bounds;
  private Quaternion quaternion;

  public ContourGenerator(Bounds bounds) {
    this.bounds = bounds;
  }

  public void setRotation(Quaternion quaternion) {
    this.quaternion = quaternion;
  }

  @Override
  public void draw(PGraphics graphics) {
    float[][] heightMap = produceGrid(
        timeStep,
        (int) (this.bounds.getWidth() / cellSize),
        (int) (this.bounds.getHeight() / cellSize));

    graphics.pushMatrix();
    float[] axis = quaternion.toAxisAngle();
    graphics.rotate(axis[0], -axis[1], axis[3], axis[2]);

    graphics.stroke(255);
    graphics.strokeWeight(1);
    // graphics.box(bounds.getWidth());

    graphics.noFill();
    graphics.stroke(255);
    graphics.strokeWeight(1);
    graphics.colorMode(PConstants.HSB);

    graphics.translate(-bounds.getWidth() / 2, -bounds.getWidth() / 2);

    for (float i = 0; i < bounds.getHeight(); i += heightIncrements) {
      float sampleHeight = i - bounds.getHeight()/2;
      graphics.stroke(i / bounds.getHeight() * colorRange, 255, 255);
      drawGridPlaneIntersection(heightMap, sampleHeight, sampleHeight * spacing, cellSize, graphics);
    }

    timeStep += updateSpeed;
    xTimeStep += xSpeed;
    yTimeStep += ySpeed;

    graphics.popMatrix();
  }

  public void setUpdateSpeed(float updateSpeed) {
    this.updateSpeed = updateSpeed;
  }

  public void setNoiseAmplitude(float noiseAmplitude) {
    this.noiseAmplitude = noiseAmplitude;
  }

  public void setXSpeed(float xSpeed) {
    this.xSpeed = xSpeed;
  }

  public void setYSpeed(float ySpeed) {
    this.ySpeed = ySpeed;
  }

  float[][] produceGrid(float t, int rows, int cols) {
    float[][] grid = new float[rows][cols];
    for (int r = 0; r < grid.length; r++) {
      for (int c = 0; c < grid[r].length; c++) {
        float x = (float) r / rows * (float) Math.PI;
        float y = (float) c / cols * (float) Math.PI;
        grid[r][c] = (Lusio.instance.noise(x + xTimeStep, y + yTimeStep, t) * noiseAmplitude) - noiseAmplitude / 2;
      }
    }
    return grid;
  }

  void drawGrid(float[][] heightMap, float gridSize, PGraphics canvas) {
    for (int r = 0; r < heightMap.length - 1; r++) {
      for (int c = 0; c < heightMap[r].length - 1; c++) {
        PVector p1 = new PVector(c * gridSize, r * gridSize, heightMap[r][c]);
        PVector p2 = new PVector((c + 1) * gridSize, r * gridSize, heightMap[r][c + 1]);
        PVector p3 = new PVector((c + 1) * gridSize, (r + 1) * gridSize, heightMap[r + 1][c + 1]);
        PVector p4 = new PVector(c * gridSize, (r + 1) * gridSize, heightMap[r + 1][c]);
        PVector m = average(p1, p2, p3, p4);
        vpoint(m, canvas);
      }
    }
  }

  void drawGridPlaneIntersection(float[][] heightMap, float planeHeight, float drawHeight, float gridSize, PGraphics canvas) {
    for (int r = 0; r < heightMap.length - 1; r++) {
      for (int c = 0; c < heightMap[r].length - 1; c++) {
        PVector p1 = new PVector(c * gridSize, r * gridSize, heightMap[r][c]);
        PVector p2 = new PVector((c + 1) * gridSize, r * gridSize, heightMap[r][c + 1]);
        PVector p3 = new PVector((c + 1) * gridSize, (r + 1) * gridSize, heightMap[r + 1][c + 1]);
        PVector p4 = new PVector(c * gridSize, (r + 1) * gridSize, heightMap[r + 1][c]);
        drawRectPlaneIntersection(p1, p2, p3, p4, planeHeight, drawHeight, canvas);
      }
    }
  }

  PVector average(PVector p1, PVector p2, PVector p3, PVector p4) {
    PVector m = new PVector();
    m.add(p1);
    m.add(p2);
    m.add(p3);
    m.add(p4);
    m.div(4);
    return m;
  }

  void drawRectPlaneIntersection(PVector p1, PVector p2, PVector p3, PVector p4, float planeHeight, float drawHeight, PGraphics canvas) {
    PVector m = average(p1, p2, p3, p4);
    drawTrianglePlaneIntersection(p1, p2, m, planeHeight, drawHeight, canvas);
    drawTrianglePlaneIntersection(p2, p3, m, planeHeight, drawHeight,canvas);
    drawTrianglePlaneIntersection(p3, p4, m, planeHeight, drawHeight,canvas);
    drawTrianglePlaneIntersection(p4, p1, m, planeHeight, drawHeight,canvas);
  }

  void drawTrianglePlaneIntersection(PVector p1, PVector p2, PVector p3, float planeHeight, float drawHeight, PGraphics canvas) {
    List<PVector> intersections = new ArrayList();
    intersections.add(intersection(p1, p2, planeHeight, drawHeight));
    intersections.add(intersection(p2, p3, planeHeight, drawHeight));
    intersections.add(intersection(p1, p3, planeHeight, drawHeight));
    intersections.removeAll(Collections.singleton(null));
    if (intersections.size() == 2) {
      PVector l1 = intersections.get(0);
      PVector l2 = intersections.get(1);
      vline(l1, l2, canvas);
    }
  }

  PVector intersection(PVector p1, PVector p2, float planeHeight, float drawHeight) {
    float t = (planeHeight - p1.z) / (p2.z - p1.z);
    float x = (p2.x - p1.x) * t + p1.x;
    float y = (p2.y - p1.y) * t + p1.y;
    if (t < 0 || t > 1) {
      return null;
    } else {
      return new PVector(x, y, drawHeight);
    }
  }

  PVector l(float t, PVector p1, PVector p2) {
    return PVector.add(PVector.mult(PVector.sub(p2, p1), t), p1);
  }

  void vline(PVector p1, PVector p2, PGraphics canvas) {
    canvas.line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
  }

  void vpoint(PVector p1, PGraphics canvas) {
    canvas.point(p1.x, p1.y, p1.z);
  }
}
