package common;

import processing.core.PVector;

public class ConstantVectorField implements VectorField {
  PVector vector;

  public ConstantVectorField(PVector vector) {
    this.vector = vector;
  }

  @Override
  public PVector at(float x, float y, float z, float t) {
    return vector;
  }
}
