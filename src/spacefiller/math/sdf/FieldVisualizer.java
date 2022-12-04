package spacefiller.math.sdf;

import processing.core.PConstants;
import processing.core.PGraphics;

public class FieldVisualizer {
  public static void drawField(
      FloatField2 field, PGraphics graphics, float resolution, float min, float max) {
    for (float x = 0; x < graphics.width; x += resolution) {
      for (float y = 0; y < graphics.height; y += resolution) {
        float value = field.get(x, y);
        graphics.noStroke();
        graphics.rectMode(PConstants.CORNER);
        if (value > 0) {
          graphics.fill((value - min) / (max - min) * 255, 0, 0);
          graphics.rect(x, y, resolution, resolution);
        } else {
          graphics.fill(0, (-value - min) / (max - min) * 255, 0);
          graphics.rect(x, y, resolution, resolution);

        }
      }
    }
  }
}
