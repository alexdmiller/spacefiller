package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.Kernel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import processing.event.KeyEvent;

public class TimeBlurEditor extends Component {
  private int width;
  private Engine engine;
  private boolean mouseDown;

  public TimeBlurEditor(PApplet applet, Engine engine, int width) {
    super(applet);
    this.width = width;
    this.engine = engine;
  }

  @Override
  protected void doDraw() {
    if (mouseDown) {
      float x = mouse.x / getWidth();
      x = Math.max(Math.min(x, 1), 0);
      engine.setTimeBlur(x);
    }

    applet.noStroke();
    applet.fill(100);
    applet.rect(0, 0, getWidth() * engine.getTimeBlur(), getHeight());
    applet.stroke(255);
    applet.noFill();
    applet.rect(0, 0, getWidth(), getHeight());

    applet.fill(255);
    applet.textAlign(PConstants.LEFT, PConstants.TOP);
    applet.text("blur", 5, 5);

    applet.textAlign(PConstants.RIGHT, PConstants.TOP);
    applet.text(engine.getTimeBlur(), getWidth(), 5);
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
