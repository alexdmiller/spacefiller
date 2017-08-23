package algoplex2;

import processing.core.PVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 8/21/17.
 */
public class Quad implements Serializable {
  private PVector topLeft;
  private PVector topRight;
  private PVector bottomLeft;
  private PVector bottomRight;

  public Quad(PVector topLeft, PVector topRight, PVector bottomLeft, PVector bottomRight) {
    this.topLeft = topLeft;
    this.topRight = topRight;
    this.bottomLeft = bottomLeft;
    this.bottomRight = bottomRight;
  }

  public Quad copy() {
    return new Quad(topLeft.copy(), topRight.copy(), bottomLeft.copy(), bottomRight.copy());
  }

  public List<PVector> getNodes() {
    List<PVector> list = new ArrayList<>();
    list.add(topLeft);
    list.add(topRight);
    list.add(bottomLeft);
    list.add(bottomRight);
    return list;
  }

  public PVector getTopLeft() {
    return topLeft;
  }

  public void setTopLeft(PVector topLeft) {
    this.topLeft = topLeft;
  }

  public PVector getTopRight() {
    return topRight;
  }

  public void setTopRight(PVector topRight) {
    this.topRight = topRight;
  }

  public PVector getBottomLeft() {
    return bottomLeft;
  }

  public void setBottomLeft(PVector bottomLeft) {
    this.bottomLeft = bottomLeft;
  }

  public PVector getBottomRight() {
    return bottomRight;
  }

  public void setBottomRight(PVector bottomRight) {
    this.bottomRight = bottomRight;
  }

  @Override
  public String toString() {
    return topLeft + ", " + topRight + ", " + bottomRight + ", " + bottomLeft;
  }
}
