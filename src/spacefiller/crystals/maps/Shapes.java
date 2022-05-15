package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

public class Shapes extends Animator.SimpleAnimator {
  interface Shape {
    void draw(PGraphics graphics);
  }

  public class Rectangle implements Shape {
    float x1, y1, x2, y2;
    int color;

    public Rectangle(int color, float x1, float y1, float x2, float y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.color = color;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.fill(color);
      graphics.rectMode(PConstants.CORNERS);
      graphics.noStroke();
      graphics.rect(
          x1 * graphics.width,
          y1 * graphics.height,
          x2 * graphics.width,
          y2 * graphics.height);
      graphics.rectMode(PConstants.CORNER);
    }
  }

  public class Square implements Shape {
    float x1, y1, size;
    int color;

    public Square(int color, float x1, float y1, float size) {
      this.x1 = x1;
      this.y1 = y1;
      this.size = size;
      this.color = color;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.fill(color);
      graphics.rectMode(PConstants.CENTER);
      graphics.noStroke();
      graphics.rect(
          x1 * graphics.width,
          y1 * graphics.height,
          size * graphics.width,
          size * graphics.width);
      graphics.rectMode(PConstants.CORNER);
    }
  }

  public class Circle implements Shape {
    float x1, y1, size;
    int color;

    public Circle(int color, float x1, float y1, float size) {
      this.x1 = x1;
      this.y1 = y1;
      this.size = size;
      this.color = color;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.fill(color);
      graphics.rectMode(PConstants.CENTER);
      graphics.noStroke();
      graphics.ellipse(
          x1 * graphics.width,
          y1 * graphics.height,
          size * graphics.width,
          size * graphics.width);
      graphics.rectMode(PConstants.CORNER);
    }
  }

  private List<Shape> shapes;

  public Shapes() {
    this.shapes = new ArrayList<>();
  }

  public Shapes square(int color, float x1, float y1, float size) {
    shapes.add(new Square(color, x1, y1, size));
    return this;
  }

  public Shapes circle(int color, float x1, float y1, float size) {
    shapes.add(new Circle(color, x1, y1, size));
    return this;
  }

  public Shapes with(int color, float x1, float y1, float x2, float y2) {
    shapes.add(new Rectangle(color, x1, y1, x2, y2));
    return this;
  }

  public Shapes red(float x1, float y1, float x2, float y2) {
    return with(0xffff0000, x1, y1, x2, y2);
  }

  public Shapes green(float x1, float y1, float x2, float y2) {
    return with(0xff00ff00, x1, y1, x2, y2);
  }

  public Shapes blue(float x1, float y1, float x2, float y2) {
    return with(0xff0000ff, x1, y1, x2, y2);
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.translate(graphics.width/2 * (1 - 1/scale), graphics.height/2 * (1 - 1/scale));
    graphics.scale(1 / scale);
    shapes.forEach(shape -> {
      shape.draw(graphics);
    });
  }
}
