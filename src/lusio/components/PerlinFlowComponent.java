package lusio.components;

import lusio.Lusio;
import particles.Bounds;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.SceneComponent;

/**
 * Created by miller on 7/16/17.
 */
public class PerlinFlowComponent extends SceneComponent {
  private float flowForce = 20;
  private float noiseScale = 500f;
  private float noiseSpeed1 = 0.01f;
  private float noiseSpeed2 = 0.01f;
  private float lineLength = 100;
  private float scrollSpeed = 1;
  private float fallSpeed = 10;
  private float lineSparsity = 0.6f;
  private float lineThickness = 2;
  private float interpolation = 0f;
  private float circleRadius = 100;
  private int numPoints = 50;
  private float scrambleSpeed = 0.01f;
  private float mainSpeed = 0.1f;

  float timeStep;
  float scramble = 0;
  float noise1Pos = 0;
  float noise2Pos = 0;

  private Bounds bounds;

  public PerlinFlowComponent(Bounds bounds) {
    this.bounds = bounds;
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

  @Override
  public void draw(PGraphics graphics) {
    graphics.pushMatrix();
    timeStep += mainSpeed;
    scramble += scrambleSpeed;
    noise1Pos += noiseSpeed1;
    noise2Pos += noiseSpeed2;

    graphics.stroke(255);
    graphics.strokeWeight(lineThickness);

    for (int j = 0; j < numPoints; j++) {
      graphics.stroke(getColorProvider().getColor(j));

      PVector p = PVector.add(
          PVector.mult(position3(j), interpolation),
          PVector.mult(position2(j), (1 - interpolation)));

      for (int i = 0; i < lineLength; i++) {

        float oldX = p.x;
        float oldY = p.y;
        PVector v = getFlow(p.x, p.y);

        p.x += v.x;
        p.y += v.y + fallSpeed;

        if (Math.sin(i + (Lusio.instance.noise((float) j) * 100.0) + timeStep * scrollSpeed) - lineSparsity < 0) {
          graphics.line(oldX, oldY, p.x, p.y);
        }
      }
    }
    graphics.popMatrix();
  }

  PVector getFlow(float x, float y) {
    float angle = (float) (Lusio.instance.noise(x / noiseScale, y / noiseScale - noise1Pos, noise2Pos) * Math.PI * 6);
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
        Lusio.instance.noise(i, 0, scramble) * bounds.getWidth() - bounds.getWidth()/ 2,
        Lusio.instance.noise(i, 1, scramble) * bounds.getHeight() - bounds.getHeight() / 2
    );
    return p;
  }
}
