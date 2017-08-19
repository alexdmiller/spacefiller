package color;

/**
 * Created by miller on 8/18/17.
 */
public class TwoColorProvider implements ColorProvider {
  private int color1;
  private int color2;
  private int color2Interval;

  public TwoColorProvider(int color1, int color2, int color2Interval) {
    this.color1 = color1;
    this.color2 = color2;
    this.color2Interval = color2Interval;
  }

  public int getColor1() {
    return color1;
  }

  public void setColor1(int color1) {
    this.color1 = color1;
  }

  public int getColor2() {
    return color2;
  }

  public void setColor2(int color2) {
    this.color2 = color2;
  }

  public int getColor2Interval() {
    return color2Interval;
  }

  public void setColor2Interval(int color2Interval) {
    this.color2Interval = color2Interval;
  }

  @Override
  public int getColor(int index) {
    return index % color2Interval == 0 ? color2 : color1;
  }
}
