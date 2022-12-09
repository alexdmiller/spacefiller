package spacefiller.math.sdf;

import java.util.List;

public class Union implements FloatField2 {
  /*
  export const smoothUnion = (k: number, ...args: Field[]) => (x: number, y: number) => {
  const res = args
      .map(f => Math.pow(2, -k * f(x, y)))
      .reduce((prev, curr) => prev + curr, 0);
  return -Math.log2(res) / k;
}
*/

  private FloatField2[] operands;
  private float k = 0.1f;

  public Union(float smooth, FloatField2... operands) {
    this.k = smooth;
    this.operands = operands;
  }

  public Union(List<FloatField2> operands, float smooth) {
    this.operands = operands.toArray(new FloatField2[] {});
    this.k = smooth;
  }

  @Override
  public float get(float x, float y) {
    float res = 0;
    for (FloatField2 f : operands) {
      res += Math.pow(2, -k * f.get(x, y));
    }
    return (float) (-(Math.log(res) / Math.log(2)) / k);
  }
}
