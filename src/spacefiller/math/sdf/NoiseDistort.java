package spacefiller.math.sdf;

import spacefiller.math.PerlinNoise;

public class NoiseDistort implements FloatField2 {
  private FloatField2 field;
  private float amplitude = 50;
  private float scale = 0.01f;

  public NoiseDistort(FloatField2 field, float amplitude, float scale) {
    this.field = field;
    this.amplitude = amplitude;
    this.scale = scale;
  }

  @Override
  public float get(float x, float y) {
    return field.get(
        x + (PerlinNoise.noise(scale * x, scale * y, 0) - 0.5f) * amplitude,
        y + + (PerlinNoise.noise(scale * x, scale * y, 100) - 0.5f) * amplitude);
  }
}
