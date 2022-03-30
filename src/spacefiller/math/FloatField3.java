package spacefiller.math;

public interface FloatField3 {
  float get(float x, float y, float z);

  FloatField3 ONE = (x, y, z) -> 1;
  FloatField3 ZERO = (x, y, z) -> 0;

  class Constant implements FloatField3 {
    private float value;

    public Constant(float value) {
      this.value = value;
    }

    @Override
    public float get(float x, float y, float z) {
      return value;
    }
  }
}