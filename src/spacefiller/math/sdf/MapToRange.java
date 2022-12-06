package spacefiller.math.sdf;

public class MapToRange implements FloatField2 {
  private FloatField2 field;
  private float inputMin;
  private float inputMax;
  private float outputMin;
  private float outputMax;

  public MapToRange(FloatField2 field, float inputMin, float inputMax, float outputMin, float outputMax) {
    this.field = field;
    this.inputMin = inputMin;
    this.inputMax = inputMax;
    this.outputMin = outputMin;
    this.outputMax = outputMax;
  }

  @Override
  public float get(float x, float y) {
    float normalized = (field.get(x, y) - inputMin) / (inputMax - inputMin);
    float mapped =  normalized * (outputMax - outputMin) + outputMin;
    return Math.max(Math.min(mapped, this.outputMax), this.outputMin);

  }
}
