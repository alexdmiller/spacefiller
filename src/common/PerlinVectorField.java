package common;

import algoplex2.Algoplex2;
import processing.core.PVector;
import toxi.math.noise.PerlinNoise;

public class PerlinVectorField implements VectorField {
  private float cellSize;
  private PerlinNoise perlin;

  public PerlinVectorField(float cellSize) {
    this.cellSize = cellSize;
    this.perlin = new PerlinNoise();
  }

  @Override
  public PVector at(float x, float y, float z, float t) {
    x = (float) Math.floor(x / cellSize) * cellSize;
    y = (float) Math.floor(y / cellSize) * cellSize;
    float theta = (float) (perlin.noise(x, y, t) * 2 * Math.PI * 4);
    PVector f = new PVector(
        (float) Math.cos(theta) * 40,
        (float) Math.sin(theta) * 40);
    return f;
  }
}
