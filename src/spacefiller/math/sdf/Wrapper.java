package spacefiller.math.sdf;

public class Wrapper implements FloatField2 {
  private FloatField2 field;

  public Wrapper() {
  }

  public Wrapper(FloatField2 field) {
    this.field = field;
  }

  public void setField(FloatField2 field) {
    this.field = field;
  }

  @Override
  public float get(float x, float y) {
    return field.get(x, y);
  }
}
