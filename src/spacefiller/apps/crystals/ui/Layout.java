package spacefiller.apps.crystals.ui;

public class Layout {
  private int x;
  private int y;
  private int padding;
  private int maxWidth;

  public Layout(int padding) {
    this.padding = padding;
    this.x = padding;
    this.y = padding;
  }

  public void placeComponent(Component component) {
    component.setPosition(x, y);
    y += component.getHeight() + padding;
    maxWidth = (int) Math.max(component.getWidth(), maxWidth);
  }

  public void newColumn() {
    y = padding;
    x += maxWidth + padding;
    maxWidth = 0;
  }
}
