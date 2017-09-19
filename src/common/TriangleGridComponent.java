package common;

import algoplex2.Grid;
import algoplex2.Quad;
import graph.Node;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import scene.SceneComponent;
import spacefiller.remote.Mod;

import java.util.Arrays;

/**
 * Created by miller on 9/12/17.
 */
public class TriangleGridComponent extends SceneComponent {
  private float t = 0;

  @Mod(min = 0, max = 20)
  public float shiftAmount = 0;

  @Mod(min = 0, max = 5)
  public float yMod = 0;

  @Mod(min = 0, max = 5)
  public float xMod = 0;

  @Mod(min = 0, max = 5)
  public float triangleMod = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private int color1;
  private int color2;

  private Grid grid;

  public TriangleGridComponent(Grid grid) {
    this.grid = grid;
  }

  public int getColor1() {
    return color1;
  }

  public void setColor1(int color1) {
    this.color1 = color1;
  }

  public int getColor2() {
    return color2;
  }

  public void setColor2(int color2) {
    this.color2 = color2;
  }

  public float getShiftAmount() {
    return shiftAmount;
  }

  public void setShiftAmount(float shiftAmount) {
    this.shiftAmount = shiftAmount;
  }

  public float getyMod() {
    return yMod;
  }

  public void setyMod(float yMod) {
    this.yMod = yMod;
  }

  public float getxMod() {
    return xMod;
  }

  public void setxMod(float xMod) {
    this.xMod = xMod;
  }

  public float getTriangleMod() {
    return triangleMod;
  }

  public void setTriangleMod(float triangleMod) {
    this.triangleMod = triangleMod;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    float shift = t + shiftAmount;

    graphics.noStroke();
    graphics.noFill();

    int quadIndex = 0;
    for (Quad quad : grid.getSquares()) {
      float row = quadIndex / grid.getColumns() - grid.getRows() / 2f;
      float col = quadIndex % grid.getColumns() - grid.getColumns() / 2f;

      int triangleIndex = 0;
      for (Node[] triangle : quad.getTriangles()) {
        float v = (float) ((Math.sin(
            row * yMod + col * xMod +
                triangleIndex / 4f * Math.PI * 2 * triangleMod
                + shift) + 1) / 2);


        graphics.fill(graphics.lerpColor(color1, color2, v));
        graphics.triangle(
            triangle[0].position.x, triangle[0].position.y,
            triangle[1].position.x, triangle[1].position.y,
            triangle[2].position.x, triangle[2].position.y);
        triangleIndex++;
      }

      quadIndex++;
    }
  }
}
