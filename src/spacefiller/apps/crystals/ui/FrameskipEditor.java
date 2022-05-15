package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.Kernel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class FrameskipEditor extends Component {
  private Kernel kernel;
  private int width;
  private int scale = 7;
  private Engine engine;

  public FrameskipEditor(PApplet applet, Engine engine, int width) {
    super(applet);
    this.width = width;
    this.kernel = new Kernel(7, applet);
    this.engine = engine;
  }

  @Override
  protected void doDraw() {
    applet.noStroke();
    applet.fill(200);
    applet.rect(0, 0, getWidth() *  ((float) (kernel.getFrameskips() + 1) / scale), getHeight());

    applet.noFill();
    applet.stroke(100);
    for (int i = 0; i < scale; i++) {
      applet.line(getWidth() / scale * i, 0, getWidth() / scale * i, getHeight());
    }

    applet.stroke(255);
    applet.rect(0, 0, getWidth(), getHeight());
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
    int x = (int) (mouse.x / (getWidth() / scale)) ;
    kernel.setFrameskips(x);
    engine.setFrameskips(x);
  }

  public void setKernel(Kernel selectedKernel) {
    this.kernel = selectedKernel;
  }
}
