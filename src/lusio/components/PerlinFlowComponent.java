package lusio.components;

import lusio.Lusio;
import particles.Bounds;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.SceneComponent;
import spacefiller.remote.Mod;
import toxi.math.noise.PerlinNoise;

/**
 * Created by miller on 7/16/17.
 */
public class PerlinFlowComponent extends SceneComponent {
  @Mod(min = 0.5f, max = 15)
  public float flowForce = 10;

  @Mod(min = 100, max = 1000)
  public float noiseScale = 500f;

  @Mod(min = 1, max = 100)
  public float lineLength = 50;

  @Mod(min = 0, max = 1)
  public float lineSparsity = 0.6f;

  @Mod(min = 0, max = 0.5f)
  public float scrollSpeed = 0.2f;

  @Mod(min = 0, max = 20)
  public float fallSpeed = 10;

  @Mod(min = 0f, max = 0.05f)
  public float noiseSpeed1 = 0.01f;

  @Mod(min = 0, max = 0.1f)
  public float mainSpeed = 0.1f;

  @Mod(min = 0, max = 0.02f)
  public float noiseSpeed2 = 0.01f;

  private float lineThickness = 2;
  private float interpolation = 0f;

  @Mod
  public float circleRadius = 100;

  private int numPoints = 50;
  private float scrambleSpeed = 0.01f;
  private boolean snapToGrid = false;
  private float gridCellSize = 10;

  float timeStep;
  float scramble = 0;
  float noise1Pos = 0;
  float noise2Pos = 0;
  float scrollPos = 0;

  private Bounds bounds;
  private PerlinNoise perlin;
  private float gridOffset;

  public PerlinFlowComponent(Bounds bounds) {
    this.bounds = bounds;
    this.perlin = new PerlinNoise();
  }

  public float getFlowForce() {
    return flowForce;
  }

  public void setFlowForce(float flowForce) {
    this.flowForce = flowForce;
  }

  public float getNoiseScale() {
    return noiseScale;
  }

  public void setNoiseScale(float noiseScale) {
    this.noiseScale = noiseScale;
  }

  public float getNoiseSpeed1() {
    return noiseSpeed1;
  }

  public void setNoiseSpeed1(float noiseSpeed1) {
    this.noiseSpeed1 = noiseSpeed1;
  }

  public float getNoiseSpeed2() {
    return noiseSpeed2;
  }

  public void setNoiseSpeed2(float noiseSpeed2) {
    this.noiseSpeed2 = noiseSpeed2;
  }

  public float getLineLength() {
    return lineLength;
  }

  public void setLineLength(float lineLength) {
    this.lineLength = lineLength;
  }

  public float getScrollSpeed() {
    return scrollSpeed;
  }

  public void setScrollSpeed(float scrollSpeed) {
    this.scrollSpeed = scrollSpeed;
  }

  public float getFallSpeed() {
    return fallSpeed;
  }

  public void setFallSpeed(float fallSpeed) {
    this.fallSpeed = fallSpeed;
  }

  public float getLineSparsity() {
    return lineSparsity;
  }

  public void setLineSparsity(float lineSparsity) {
    this.lineSparsity = lineSparsity;
  }

  public float getLineThickness() {
    return lineThickness;
  }

  public void setLineThickness(float lineThickness) {
    this.lineThickness = lineThickness;
  }

  public float getInterpolation() {
    return interpolation;
  }

  public void setInterpolation(float interpolation) {
    this.interpolation = interpolation;
  }

  public float getCircleRadius() {
    return circleRadius;
  }

  public void setCircleRadius(float circleRadius) {
    this.circleRadius = circleRadius;
  }

  public int getNumPoints() {
    return numPoints;
  }

  public void setNumPoints(int numPoints) {
    this.numPoints = numPoints;
  }

  public float getScrambleSpeed() {
    return scrambleSpeed;
  }

  public void setScrambleSpeed(float scrambleSpeed) {
    this.scrambleSpeed = scrambleSpeed;
  }

  public void setMainSpeed(float mainSpeed) {
    this.mainSpeed = mainSpeed;
  }

  public boolean isSnapToGrid() {
    return snapToGrid;
  }

  public void setSnapToGrid(boolean snapToGrid) {
    this.snapToGrid = snapToGrid;
  }

  public float getGridCellSize() {
    return gridCellSize;
  }

  public void setGridCellSize(float gridCellSize) {
    this.gridCellSize = gridCellSize;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.pushMatrix();
    timeStep += mainSpeed;
    scramble += scrambleSpeed;
    noise1Pos += noiseSpeed1;
    noise2Pos += noiseSpeed2;
    scrollPos += scrollSpeed;

    graphics.stroke(255);
    graphics.strokeWeight(lineThickness);

    if (snapToGrid) {
      graphics.pushMatrix();
      graphics.translate(-bounds.getWidth()/2, -bounds.getHeight()/2);
      int index = 0;
      for (float x = 0; x < bounds.getWidth(); x += gridCellSize) {
        for (float y = 0; y < bounds.getHeight(); y += gridCellSize) {
          index++;
          graphics.stroke(getColorProvider().getColor(index));
          PVector p = new PVector(x + gridOffset, y + gridOffset);
          drawLine(index, p, graphics);
        }
      }
      graphics.popMatrix();
    } else {
      for (int j = 0; j < numPoints; j++) {
        graphics.stroke(getColorProvider().getColor(j));

        PVector p = PVector.add(
            PVector.mult(position3(j), interpolation),
            PVector.mult(position2(j), (1 - interpolation)));

        drawLine(j, p, graphics);
      }
    }


    graphics.popMatrix();
  }

  private void drawLine(int index, PVector p, PGraphics graphics) {
    for (int i = 0; i < lineLength; i++) {
      float oldX = p.x;
      float oldY = p.y;
      PVector v = getFlow(p.x, p.y);

      p.x += v.x + fallSpeed;
      p.y += v.y;

      if (Math.sin(i + (perlin.noise((float) index) * 100.0) + scrollPos) - lineSparsity < 0) {
        graphics.line(oldX, oldY, p.x, p.y);
      }
    }
  }

  PVector getFlow(float x, float y) {
    float angle = (float) (perlin.noise(x / noiseScale + noise1Pos, y / noiseScale, noise2Pos) * Math.PI * 6);
    return PVector.fromAngle(angle).setMag(flowForce);
  }

  PVector position1(int i) {
    return new PVector(((float) bounds.getWidth() / numPoints) * i - bounds.getWidth() / 2, -bounds.getHeight() / 2);
  }

  PVector position2(int i) {
    float theta = (float) (2 * Math.PI * (float) i / numPoints + timeStep);
    PVector p = new PVector(
        (float) Math.cos(theta) * circleRadius,
        (float) Math.sin(theta) * circleRadius
    );
    return p;
  }

  PVector position3(int i) {
    PVector p = new PVector(
        perlin.noise(i, 0, scramble) * bounds.getWidth() * 2 - bounds.getWidth(),
        perlin.noise(i, 1, scramble) * bounds.getHeight() * 2 - bounds.getHeight()
    );
    return p;
  }


  public void setGridOffset(float gridOffset) {
    this.gridOffset = gridOffset;
  }
}
