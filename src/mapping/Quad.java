package mapping;

import processing.core.PVector;

/**
 * Created by miller on 11/18/17.
 */
public class Quad {
  public PVector topLeft;
  public PVector topRight;
  public PVector bottomRight;
  public PVector bottomLeft;


  public Quad(PVector topLeft, PVector bottomRight) {
    this(
        topLeft,
        new PVector(bottomRight.x, topLeft.y),
        bottomRight,
        new PVector(topLeft.x, bottomRight.y));
  }

  public Quad(PVector topLeft, PVector topRight, PVector bottomRight, PVector bottomLeft) {
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.bottomRight = bottomRight;
    this.bottomLeft = bottomLeft;

  }

  public String toString() {
    return topLeft + ", " + topRight + ", " + bottomRight + ", " + bottomLeft;
  }
}
