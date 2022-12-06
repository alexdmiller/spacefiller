package spacefiller.math.sdf;

public class Add implements FloatField2 {
  private FloatField2 field;
  private float offset;

  public Add(FloatField2 field, float offset) {
    this.field = field;
    this.offset = offset;
  }

  @Override
  public float get(float x, float y) {
    return field.get(x, y) + offset;
  }
}
