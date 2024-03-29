package spacefiller.math;

import processing.core.PGraphics;

public interface FloatField2 {
  float GRADIENT_OFFSET = 1;

  static Vector gradient(FloatField2 field, Vector position) {
    float left = field.get(position.x - GRADIENT_OFFSET, position.y);
    float right = field.get(position.x + GRADIENT_OFFSET, position.y);
    float top = field.get(position.x, position.y - GRADIENT_OFFSET);
    float bottom = field.get(position.x, position.y + GRADIENT_OFFSET);

    return new Vector(left - right, top - bottom);
  }

  static void debugDraw(FloatField2 field, float resolution, PGraphics canvas) {
    canvas.noStroke();
    for (int y = 0; y < canvas.height; y += resolution) {
      for (int x = 0; x < canvas.width; x += resolution) {
        canvas.fill(0, 0, field.get(x, y) * 255);
        canvas.rect(
            x - resolution / 2,
            y - resolution / 2,
            resolution,
            resolution);
      }
    }
  }

  float get(float x, float y);

  FloatField2 ONE = (x, y) -> 1;

  class Constant implements FloatField2 {
    private float value;

    public Constant(float value) {
      this.value = value;
    }

    @Override
    public float get(float x, float y) {
      return value;
    }
  }

}


