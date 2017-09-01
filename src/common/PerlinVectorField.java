package common;

import algoplex2.Algoplex2;
import processing.core.PVector;

public class PerlinVectorField implements VectorField {
  private float cellSize;

  public PerlinVectorField(float cellSize) {
    this.cellSize = cellSize;
  }

  @Override
  public PVector at(float x, float y, float z, float t) {
    x = (float) Math.floor(x / cellSize) * cellSize;
    y = (float) Math.floor(y / cellSize) * cellSize;
    float theta = (float) (Algoplex2.instance.noise(x, y, t) * 4 * Math.PI);
    PVector f = new PVector(
        (float) Math.cos(theta) * 40,
        (float) Math.sin(theta) * 40);
    return f;
  }
}
