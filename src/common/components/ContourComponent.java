package common.components;

import spacefiller.remote.Mod;
import particles.Bounds;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.SceneComponent;
import toxi.geom.Quaternion;
import toxi.math.noise.PerlinNoise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by miller on 7/22/17.
 */
public class ContourComponent extends SceneComponent {
  @Mod(min = 0, max = 5)
  public float noiseAmplitude = 0;

  @Mod(min = 0, max = 0.1f)
  public float updateSpeed = 0.01f;

  @Mod(min = 0, max = 0.1f)
  public float xSpeed = 0f;

  @Mod(min = 0, max = 0.1f)
  public float ySpeed = 0f;

  private float timeStep = 0;
  private float stripeTimeStep = 0;

  private float xTimeStep = 0;
  private float yTimeStep = 0;

  @Mod(min = 0, max = 10)
  public float noiseScale = 1;

  @Mod(min = 0, max = 10)
  public float lineSize = 1;

  public int slices = 5;

  private int color;

  private Bounds bounds;
  private Quaternion quaternion = Quaternion.createFromEuler(0, 0, 0);

  private PerlinNoise perlin;

  @Mod(min = 0, max = 50)
  public int resolution = 10;

  @Mod(min = 0.001f, max = 0.1f)
  public float stripeSize;

  @Mod(min = -0.1f, max = 0.1f)
  public float stripeUpdateSpeed;

  public boolean drawGrid = false;

  public ContourComponent(Bounds bounds) {
    this.bounds = bounds;
    this.perlin = new PerlinNoise();
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setRotation(Quaternion quaternion) {
    this.quaternion = quaternion;
  }

  public void setNoiseScale(float noiseScale) {
    this.noiseScale = noiseScale;
  }

  @Override
  public void draw(PGraphics graphics) {
    timeStep += updateSpeed;
    stripeTimeStep += stripeUpdateSpeed;
    xTimeStep += xSpeed;
    yTimeStep += ySpeed;


    float[][] heightMap = produceGrid(
        timeStep,
        (int) (resolution / bounds.getWidth() * bounds.getHeight()) + 1,
        resolution + 1);


    // drawGrid(heightMap, bounds.getWidth() / resolution, graphics);

    graphics.pushMatrix();

    float[] axis = quaternion.toAxisAngle();
    graphics.rotate(axis[0], axis[1], axis[3], axis[2]);

    graphics.translate(-bounds.getWidth() / 2, -bounds.getHeight() / 2, -bounds.getDepth() / 2);

    for (int i = 0; i < slices; i++) {
      graphics.strokeWeight(lineSize);
      //graphics.stroke(255);

      // TODO: figure out color!
//      graphics.stroke(graphics.lerpColor(
//          color,
//          0xFFFFFFFF,
//          (float) (Math.sin(i * stripeSize + stripeTimeStep) + 1) / 2));

      graphics.stroke(graphics.lerpColor(
          color,
          0xFFFFFFFF,
          (float) i / slices));

      drawSlice(
          heightMap, (float) i / slices, bounds.getDepth() * i / slices, bounds.getWidth() / resolution, graphics);
    }

    if (drawGrid) {
      graphics.pushMatrix();
      graphics.noFill();
      graphics.translate(bounds.getWidth() / 2, bounds.getHeight() / 2, bounds.getDepth() / 2);
      graphics.box(bounds.getWidth(), bounds.getHeight(), bounds.getDepth());
      graphics.popMatrix();

      drawGrid(heightMap, bounds.getWidth() / resolution, graphics);
    }

    graphics.popMatrix();
  }

  public void setLineSize(float lineSize) {
    this.lineSize = lineSize;
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

  public void setColor(int color) {
    this.color = color;
  }

  float[][] produceGrid(float t, int rows, int cols) {
    float[][] grid = new float[rows][cols];
    for (int r = 0; r < grid.length; r++) {
      for (int c = 0; c < grid[r].length; c++) {
        float x = (float) r / rows * (float) Math.PI;
        float y = (float) c / cols * (float) Math.PI;
        grid[r][c] = (this.perlin.noise((x + xTimeStep) / noiseScale, (y + yTimeStep) / noiseScale, t) * noiseAmplitude) - noiseAmplitude / 2;
      }
    }
    return grid;
  }

  void drawGrid(float[][] heightMap, float gridSize, PGraphics canvas) {
    canvas.strokeWeight(10);
    canvas.stroke(255);
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

  void drawSlice(float[][] heightMap, float planeHeight, float drawHeight, float gridSize, PGraphics canvas) {
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
