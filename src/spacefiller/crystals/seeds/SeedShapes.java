package crystal.seeds;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

public class SeedShapes extends Animator.SimpleAnimator {
  interface Shape {
    void draw(PGraphics graphics);
  }

  public class Rectangle implements Shape {
    float x1, y1, x2, y2;

    public Rectangle(float x1, float y1, float x2, float y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.noFill();
      graphics.rectMode(PConstants.CORNERS);
      graphics.stroke(255);
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
    public Square(float x1, float y1, float size) {
      this.x1 = x1;
      this.y1 = y1;
      this.size = size;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.rectMode(PConstants.CENTER);
      graphics.stroke(255);
      graphics.rect(
          Math.round(x1 * graphics.width),
          Math.round(y1 * graphics.height),
          Math.round(size * graphics.width),
          Math.round(size * graphics.width));
      graphics.rectMode(PConstants.CORNER);
    }
  }

  public class Circle implements Shape {
    float x1, y1, size;

    public Circle(float x1, float y1, float size) {
      this.x1 = x1;
      this.y1 = y1;
      this.size = size;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.rectMode(PConstants.CENTER);
      graphics.stroke(255);
      graphics.ellipse(
          x1 * graphics.width,
          y1 * graphics.height,
          size * graphics.width,
          size * graphics.width);
      graphics.rectMode(PConstants.CORNER);
    }
  }

  public class Line implements Shape {
    float x1, y1, x2, y2, weight;

    public Line(float x1, float y1, float x2, float y2, float weight) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.weight = weight;
    }

    @Override
    public void draw(PGraphics graphics) {
      graphics.stroke(255);
      graphics.strokeWeight(graphics.strokeWeight * weight);
      graphics.line(
          x1 * graphics.width,
          y1 * graphics.height,
          x2 * graphics.width,
          y2 * graphics.height);
    }
  }

  private List<Shape> shapes;

  public SeedShapes() {
    this.shapes = new ArrayList<>();
  }

  public SeedShapes square(float x1, float y1, float size) {
    shapes.add(new Square(x1, y1, size));
    return this;
  }

  public SeedShapes circle(float x1, float y1, float size) {
    shapes.add(new Circle(x1, y1, size));
    return this;
  }

  public SeedShapes rect(float x1, float y1, float x2, float y2) {
    shapes.add(new Rectangle(x1, y1, x2, y2));
    return this;
  }

  public SeedShapes line(float x1, float y1, float x2, float y2, float weight) {
    shapes.add(new Line(x1, y1, x2, y2, weight));
    return this;
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.translate(graphics.width/2 * (1 - 1/scale), graphics.height/2 * (1 - 1/scale));
    graphics.scale(1 / scale);
    graphics.noFill();
    graphics.strokeWeight(scale);
    shapes.forEach(shape -> {
      shape.draw(graphics);
    });
  }

  @Override
  public void preview(PGraphics graphics) {
    graphics.translate(graphics.width/2 * (1 - 1/1), graphics.height/2 * (1 - 1/1));
    graphics.scale(1 / 1);
    graphics.noFill();
    graphics.strokeWeight(10);
    shapes.forEach(shape -> {
      shape.draw(graphics);
    });
  }
}
