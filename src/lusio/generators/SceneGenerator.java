package lusio.generators;

import common.Bounds;
import processing.core.PGraphics;

/**
 * Created by miller on 7/12/17.
 */
public abstract class SceneGenerator {
  private Bounds bounds;
  private float x;
  private float y;

  public abstract void setup();
  public abstract void draw(PGraphics graphics);

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public void setPos(float x, float y) {
    this.x = x;
    this.y = y;
  }
}
