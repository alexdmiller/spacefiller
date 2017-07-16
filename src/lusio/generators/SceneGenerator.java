package lusio.generators;

import particles.Bounds;
import processing.core.PGraphics;

/**
 * Created by miller on 7/12/17.
 */
public abstract class SceneGenerator {
  private float x;
  private float y;

  public abstract void draw(PGraphics graphics);

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
