package spacefiller.math.sdf;

public class Floor implements FloatField2 {
  private FloatField2 field2;

  public Floor(FloatField2 field2) {
    this.field2 = field2;
  }

  @Override
  public float get(float x, float y) {
    return Math.max(field2.get(x, y), 0);
  }
}
