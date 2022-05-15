package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class ScaleEditor extends Component {
  private int width;
  private int minScale;
  private int maxScale;
  private Engine engine;
  private boolean mouseDown;

  public ScaleEditor(PApplet applet, Engine engine, int width, int minScale, int maxScale) {
    super(applet);
    this.width = width;
    this.engine = engine;
    this.minScale = minScale;
    this.maxScale = maxScale;
  }

  @Override
  protected void doDraw() {
    if (mouseDown) {
      float x = mouse.x / getWidth();
      x = Math.max(Math.min(x, 1), 0);
      engine.setScale((float) (Math.pow (x, 4) * (maxScale - minScale) + minScale));
    }

    applet.noStroke();
    applet.fill(100);
    applet.rect(0, 0, getWidth() * engine.getScale() / (maxScale - minScale), getHeight());
    applet.stroke(255);
    applet.noFill();
    applet.rect(0, 0, getWidth(), getHeight());

    applet.fill(255);
    applet.textAlign(PConstants.LEFT, PConstants.TOP);
    applet.text("scale", 5, 5);

    applet.textAlign(PConstants.RIGHT, PConstants.TOP);
    applet.text(engine.getScale(), getWidth(), 5);
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    return 20;
  }

  @Override
  public void mousePressed(PVector mouse) {
    mouseDown = true;
  }

  @Override
  public void mouseReleased(PVector mouse) {
    mouseDown = false;
  }
}
