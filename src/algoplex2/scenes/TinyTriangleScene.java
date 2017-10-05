package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Quad;
import graph.*;
import processing.core.PConstants;
import processing.core.PVector;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.math.noise.PerlinNoise;

import static processing.core.PConstants.BLEND;
import static processing.core.PConstants.P2D;
import static processing.core.PConstants.P3D;

public class TinyTriangleScene extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.002f;

  private PerlinNoise perlin;

  @Mod(min = 0.5f, max = 0.6f)
  public float threshold = 0.5f;

  @Mod(min = 0.0005f, max = 0.002f)
  public float scale1 = 0.001f;

  @Mod(min = 0.0005f, max = 0.002f)
  public float scale2 = 0.001f;

  @Mod(min = 0, max = 10)
  public float offset;

  @Mod(min = 0.005f, max = 0.01f)
  public float scale = 0.005f;

  @Mod(min = 0, max = 0.6283185307179586f)
  public float color1Rotation;

  @Mod(min = 0, max = -0.39269908169872414f)
  public float color2Rotation;

  @Mod(min = 0.01f, max = 0.1f)
  public float circleScale = 1;

  @Mod(min = 0, max = 1)
  public float interpolation;

  private PVector translate = new PVector();
  private int color;

  public TinyTriangleScene() {
    perlin = new PerlinNoise();
  }

  public void setup() {
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(color1Rotation);
    TColor color2 = color1.getRotatedRYB(color2Rotation);
    TColor color3 = color1.getRotatedRYB(color2Rotation * 2);
    TColor color4 = color1.getRotatedRYB(color2Rotation * 3);

    graphics.noStroke();
    for (Quad quad : grid.getSquares()) {
      translate.x = quad.getTopLeft().position.x;
      translate.y = quad.getTopLeft().position.y;
//      graphics.pushMatrix();
//      graphics.translate(quad.getTopLeft().position.x, quad.getTopLeft().position.y);
      color = graphics.color(color1.toARGB());
      triangleTop(graphics, (int) quad.getWidth(), (int) quad.getHeight());
      color = graphics.color(color2.toARGB());
      triangleLeft(graphics, (int) quad.getWidth(), (int) quad.getHeight());
      color = graphics.color(color3.toARGB());
      triangleRight(graphics, (int) quad.getWidth(), (int) quad.getHeight());
      color = graphics.color(color4.toARGB());
      triangleBot(graphics, (int) quad.getWidth(), (int) quad.getHeight());
//      graphics.popMatrix();
    }
    super.draw(graphics);
  }

  private void triangleTop(PGraphics graphics, int width, int height) {
    for (int i = 0; i < width; i += width / 3) {
      triangle(i, 0, i + (width / 3), 0, (i + (i + width / 3)) / 2, height / 6, graphics);
    }
    for (int i = width / 3; i <= width * 2 / 3; i += width / 3) {
      triangle(i, 0, i - (width / 6), height / 6, i + (width / 6), height / 6, graphics);
    }
    for (int i = width / 6; i <= width * 3 / 6; i += width / 3) {
      triangle(i, height / 6, i + (width / 3), height / 6, (i + (i + width / 3)) / 2, height / 3, graphics);
    }
    triangle(width / 2, height / 6, (width / 2) - (width / 6), height / 3, (width / 2) + (width / 6), height / 3, graphics);
    triangle(width / 2, height / 2, (width / 2) - (width / 6), height / 3, (width / 2) + (width / 6), height / 3, graphics);
  }

  private void triangleRight(PGraphics graphics, int width, int height) {
    for (int i = 0; i < height; i += height / 3) {
      triangle(width, i, width, i + (height / 3), width * 5 / 6, (i + (i + height / 3)) / 2, graphics);
    }
    for (int i = height / 3; i <= height * 2 / 3; i += height / 3) {
      triangle(width, i, width*5 / 6, i - (height / 6), width*5 / 6, i + (height / 6), graphics);
    }
    for (int i = height / 6; i <= height * 3 / 6; i += height / 3) {
      triangle( width*5 / 6, i, width*5 / 6,i + (height / 3),  width*2 / 3,(i + (i + height / 3)) / 2, graphics);
    }
    triangle(width*5 / 6,height / 2,  width*2 / 3,(height / 2) - (height / 6),  width*2 / 3,(height / 2) + (height / 6), graphics);
    triangle(width / 2,height / 2,  width*2 / 3,(height / 2) - (height / 6),   width*2 / 3,(height / 2) + (height / 6), graphics);
  }


  private void triangleBot(PGraphics graphics, int width, int height){
    for (int i = 0; i < width; i += width / 3) {
      triangle(i, height, i + (width / 3), height, (i + (i + width / 3)) / 2, height * 5 / 6, graphics);
    }
    for (int i = width / 3; i <= width * 2 / 3; i += width / 3) {
      triangle(i, height, i - (width / 6), height*5 / 6, i + (width / 6), height*5 / 6, graphics);
    }
    for (int i = width / 6; i <= width * 3 / 6; i += width / 3) {
      triangle(i, height * 5 / 6, i + (width / 3), height * 5 / 6, (i + (i + width / 3)) / 2, height * 2 / 3, graphics);
    }
    triangle(width / 2, height*5 / 6, (width / 2) - (width / 6), height*2 / 3, (width / 2) + (width / 6), height*2 / 3, graphics);
    triangle(width / 2, height / 2, (width / 2) - (width / 6), height*2 / 3, (width / 2) + (width / 6), height*2 / 3, graphics);
    //TriangleRight
  }

  private void triangleLeft(PGraphics graphics, int width, int height){
    for (int i = 0; i < height; i += height / 3) {
      triangle(0, i, 0, i + (height / 3), width / 6, (i + (i + height / 3)) / 2, graphics);
    }
    for (int i = height / 3; i <= height * 2 / 3; i += height / 3) {
      triangle(0, i, width / 6, i - (height / 6), width / 6, i + (height / 6), graphics);
    }
    for (int i = height / 6; i <= height * 3 / 6; i += height / 3) {
      triangle( width / 6, i, width / 6,i + (height / 3),  width / 3,(i + (i + height / 3)) / 2, graphics);
    }
    triangle(width / 6,height / 2,  width / 3,(height / 2) - (height / 6),  width / 3,(height / 2) + (height / 6), graphics);
    triangle(width / 2,height / 2,  width / 3,(height / 2) - (height / 6),   width / 3,(height / 2) + (height / 6), graphics);
  }

  private void triangle(float x1, float y1, float x2, float y2, float x3, float y3, PGraphics graphics) {
    x1 += translate.x;
    y1 += translate.y;
    x2 += translate.x;
    y2 += translate.y;
    x3 += translate.x;
    y3 += translate.y;

//    float sum = perlin.noise(x1 * scale, y1 * scale, t + offset)
//        + perlin.noise(x2 * scale, y2 * scale, t + offset)
//        + perlin.noise(x3 * scale, y3 * scale, t + offset);

    float sum = sample(x1, y1) + sample(x2, y2) + sample(x3, y3);

    sum /= 3f;

//    graphics.stroke(0);
//    graphics.strokeWeight(1);
//    graphics.noFill();
    if (sum > threshold) {
      //graphics.fill((float) (Math.pow(sum, 4) * 255));
      graphics.fill(color, (float) Math.pow(sum, 2) * 255);
      graphics.triangle(x1, y1, x2, y2, x3, y3);
    }
  }

  private float sample(float x, float y) {
    return circle(x, y) * interpolation + noise(x, y) * (1 - interpolation);
  }

  private float circle(float x, float y) {
    float dx = x - grid.getWidth() / 2;
    float dy = y - grid.getHeight() / 2;
    return (float) Math.sin(Math.sqrt(dx*dx + dy*dy) * circleScale + t);
  }

  private float noise(float x, float y) {
    float n = perlin.noise(x * scale, y * scale, t / 5f);
    if (n > 0.6) {
      return 1;
    } else {
      return n;
    }
  }
}
