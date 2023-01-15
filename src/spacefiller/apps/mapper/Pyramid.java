package spacefiller.apps.mapper;

import processing.core.PGraphics;
import spacefiller.math.Vector;

import java.io.Serializable;

public class Pyramid implements Serializable {
  private static final long serialVersionUID = -1039788894126266349L;

  Vector bottomLeft;
  Vector bottomRight;
  Vector topLeft;
  Vector topRight;
  Vector center;

  public Pyramid(Vector center, int size) {
    this.bottomLeft = new Vector(center.x - size, center.y + size);
    this.bottomRight = new Vector(center.x + size, center.y + size);
    this.topLeft = new Vector(center.x -size, center.y -size);
    this.topRight = new Vector(center.x + size, center.y -size);
    this.center = new Vector(center.x + 0, center.y + 0);
  }

  void draw(PGraphics graphics, int frame, int index) {
    graphics.stroke(255);
    graphics.fill((int) ((Math.sin((float) frame / 10f + index) + 1) / 2 * 255));
    graphics.triangle(
        bottomLeft.x, bottomLeft.y,
        bottomRight.x, bottomRight.y,
        center.x, center.y);
    graphics.triangle(
        bottomRight.x, bottomRight.y,
        topRight.x, topRight.y,
        center.x, center.y);
    graphics.triangle(
        topRight.x, topRight.y,
        topLeft.x, topLeft.y,
        center.x, center.y);
    graphics.triangle(
        topLeft.x, topLeft.y,
        bottomLeft.x, bottomLeft.y,
        center.x, center.y);
  }

  void set(float x, float y) {
    Vector delta = Vector.sub(new Vector(x, y), center);
    this.bottomLeft.add(delta);
    this.bottomRight.add(delta);
    this.topLeft.add(delta);
    this.topRight.add(delta);
    this.center.add(delta);
  }

  Vector selectVector(Vector mouse) {
    if (bottomLeft.dist(mouse) < 5) {
      return bottomLeft;
    }
    if (bottomRight.dist(mouse) < 5) {
      return bottomRight;
    }
    if (topRight.dist(mouse) < 5) {
      return topRight;
    }
    if (topLeft.dist(mouse) < 5) {
      return topLeft;
    }
    if (center.dist(mouse) < 5) {
      return center;
    }
    return null;
  }
}