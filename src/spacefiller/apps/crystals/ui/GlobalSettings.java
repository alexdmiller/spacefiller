package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class GlobalSettings extends Component {
  private Engine engine;

  private int checkboxSize = 10;
  private int padding = 10;

  public GlobalSettings(PApplet applet, Engine engine) {
    super(applet);
    this.engine = engine;
  }

  private void drawCheckbox(String text, boolean checked, int y) {
    applet.pushMatrix();
    applet.translate(0, y);
    applet.stroke(255);
    applet.noFill();
    applet.rect(0, 0, checkboxSize, checkboxSize);

    applet.fill(255);
    applet.textAlign(PConstants.LEFT, PConstants.BOTTOM);

    applet.text(text, 20, 0 + checkboxSize);

    if (checked) {
      applet.fill(255);
      applet.rect(0, 0, checkboxSize, checkboxSize);
    }
    applet.popMatrix();
  }

  @Override
  protected void doDraw() {
    drawCheckbox("on", engine.isOn(), 0);
    drawCheckbox("sticky kernels", engine.isStickyKernels(), 1 * (checkboxSize + padding));
    drawCheckbox("sticky maps", engine.isStickyMaps(), 2 * (checkboxSize + padding));
    drawCheckbox("sticky seeds", engine.isStickySeeds(), 3 * (checkboxSize + padding));
  }

  private void toggleCheckbox(int i) {
    switch (i) {
      case 0: engine.toggleOn(); break;
      case 1: engine.toggleStickyKernels(); break;
      case 2: engine.toggleStickyMaps(); break;
      case 3: engine.toggleStickySeeds(); break;
      default:
        break;
    }
  }

  @Override
  public void mousePressed(PVector mouse) {
    super.mousePressed(mouse);

    for (int i = 0; i < 4; i++) {
      if (mouse.x >= 0 &&
          mouse.x <= checkboxSize &&
          mouse.y >= i * (checkboxSize + padding) &&
          mouse.y <= i * (checkboxSize + padding) + checkboxSize) {
        toggleCheckbox(i);
      }
    }
  }

  @Override
  public float getWidth() {
    return 200;
  }

  @Override
  public float getHeight() {
    return 20 * 4;
  }
}
