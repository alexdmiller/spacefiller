package spacefiller.math.sdf;

public class Normalize implements FloatField2 {
  private float width;
  private float height;
  private float sampleResolution;
  private FloatField2 field;

  private float min;
  private float max;

  public Normalize(float width, float height, float sampleResolution, FloatField2 field) {
    this.width = width;
    this.height = height;
    this.sampleResolution = sampleResolution;
    this.field = field;

    this.recompute();
  }

  public void recompute() {
    // TODO: use min?
    min = 0;
    max = 0;
    for (float sx = 0; sx < width; sx += sampleResolution) {
      for (float sy = 0; sy < height; sy += sampleResolution) {
        float sample = field.get(sx, sy);
        max = Math.max(max, sample);
      }
    }
  }

  @Override
  public float get(float x, float y) {
    return (field.get(x, y) - min) / (max - min);
//    return field.get(x, y);
  }
}
