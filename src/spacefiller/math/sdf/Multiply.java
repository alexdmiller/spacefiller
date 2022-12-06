package spacefiller.math.sdf;

public class Multiply implements FloatField2 {
  private FloatField2 field;
  private float multiplier;

  public Multiply(FloatField2 field, float multiplier) {
    this.field = field;
    this.multiplier = multiplier;
  }

  @Override
  public float get(float x, float y) {
    return field.get(x, y) * multiplier;
  }
}
