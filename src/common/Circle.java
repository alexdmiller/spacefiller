package common;

import processing.core.PVector;

public class Circle {
  public PVector position, velocity = new PVector();
  public float radius;

  public Circle(float x, float y, float radius) {
    this.position = new PVector(x, y);
    this.velocity = PVector.random2D();
    this.radius = radius;
  }

  public void update(float width, float height) {
    //this.velocity.mult(0.95);
    if (position.x < 0) {
      velocity.x *= -1;
      position.x = 0;
    } else if (position.x > width) {
      velocity.x *= -1;
      position.x = width;
    }

    if (position.y < 0) {
      velocity.y *= -1;
      position.y = 0;
    } else if (position.y > height) {
      velocity.y *= -1;
      position.y = height;
    }

    this.position.add(velocity);
  }
}