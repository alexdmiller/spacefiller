package spacefiller.math.sdf;

import spacefiller.math.Vector;

public class Circle implements FloatField2 {
  private Vector position;
  private float radius;

  public Circle(float x, float y, float radius) {
    this.position = new Vector(x, y);
    this.radius = radius;
  }

  public void setPosition(Vector position) {
    this.position.set(position);
  }

  @Override
  public float get(float x, float y) {
    return position.dist(x, y) - radius;
  }
}