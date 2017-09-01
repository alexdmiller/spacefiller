package color;

/**
 * Created by miller on 8/19/17.
 */
public class ConstantColorProvider implements ColorProvider {
  public static final ColorProvider WHITE = new ConstantColorProvider(0xFFFFFFFF);

  private int color;

  public ConstantColorProvider(int color) {
    this.color = color;
  }

  @Override
  public int getColor(int index) {
    return color;
  }
}
