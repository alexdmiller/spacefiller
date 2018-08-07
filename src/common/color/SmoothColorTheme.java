package common.color;

import toxi.color.*;
import toxi.math.CosineInterpolation;
import toxi.math.InterpolateStrategy;

public class SmoothColorTheme {
  private ColorList cols;

  public SmoothColorTheme(ColorRange range, int numColors, int resolution) {
    ColorList colors = range.getColors(numColors);
    colors.sortByDistance(false);

    ColorGradient gradient = new ColorGradient();

    gradient.setInterpolator(new CosineInterpolation());
    for (int i = 0; i < colors.size() - 1; i++) {
      gradient.addColorAt(i / (float) numColors * resolution, colors.get(i));
    }
    gradient.addColorAt(resolution, colors.get(0));

    cols = gradient.calcGradient(0, resolution);
  }

  public ReadonlyTColor getColor(float angle) {
    float wrapped = (float) (angle % (2 * Math.PI));
    float normalized = (float) (wrapped / (2 * Math.PI));
    int index = (int) (normalized * cols.size());

    return cols.get(index);
  }
}
