package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Kernel;
import processing.core.PApplet;
import processing.core.PConstants;

import static crystal.engine.Kernel.MAX_THRESHOLD;

public class ThresholdEditor extends Component {
  private Kernel kernel;

  public ThresholdEditor(PApplet applet) {
    super(applet);
    kernel = new Kernel(7, applet);
  }

  public Kernel getKernel() {
    return kernel;
  }

  public void setKernel(Kernel kernel) {
    this.kernel = kernel;
  }

  @Override
  public void doDraw() {
    if (mouseDown) {
      int x = (int) Math.floor((lastMouseClick.x) / (getWidth() / 3));
      float mouseDist = (lastMouse.y - mouse.y) / 10;
      kernel.addToThreshold(x, mouseDist);
    }

    float[] thresholds = kernel.getThresholds();
    applet.rectMode(PConstants.CORNERS);

    float scale = getHeight() / MAX_THRESHOLD;
    applet.noStroke();

    applet.fill(255, 0, 0);
    applet.rect(
        0,
        getHeight() - thresholds[0] * scale,
        getWidth() / 3,
        getHeight());

    applet.fill(0, 255, 0);
    applet.rect(
        getWidth() / 3,
        getHeight() - thresholds[1] * scale,
        2 * getWidth() / 3,
        getHeight());

    applet.fill(0, 0, 255);
    applet.rect(
        2 * getWidth() / 3,
        getHeight() - thresholds[2] * scale,
        getWidth(),
        getHeight());

    applet.stroke(100);
    applet.noFill();
    applet.line(getWidth() / 3, 0, getWidth() / 3, getHeight());
    applet.line(2 * getWidth() / 3, 0, 2 * getWidth() / 3, getHeight());

    applet.stroke(255);
    applet.rect(0, 0, getWidth(), getHeight());
  }

  @Override
  public float getWidth() {
    return 7 * 7 * 3;
  }

  @Override
  public float getHeight() {
    return 70;
  }
}
