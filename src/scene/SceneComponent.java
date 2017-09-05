package scene;

import common.color.ColorProvider;
import processing.core.PGraphics;

public abstract class SceneComponent {
  private float x;
  private float y;
  private ColorProvider colorProvider;

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

  public ColorProvider getColorProvider() {
    return colorProvider;
  }

  public void setColorProvider(ColorProvider colorProvider) {
    this.colorProvider = colorProvider;
  }
}
