package algoplex2;

import graph.renderer.CrosshairGraphRenderer;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.color.TColor;

/**
 * Created by miller on 10/4/17.
 */
public class TransitionAnimation {
  private float t;

  CrosshairGraphRenderer crosshairGraphRenderer;

  public TransitionAnimation() {
    crosshairGraphRenderer = new CrosshairGraphRenderer();
  }

  public void reset() {
    t = 0;
  }

  public void draw(PGraphics graphics, Grid grid) {
    t++;

    graphics.colorMode(PConstants.RGB);
    crosshairGraphRenderer.setColor(graphics.color(255, 255 - t * 5));
    crosshairGraphRenderer.size = 20 + t / 10;
    crosshairGraphRenderer.thickness = 2;
    crosshairGraphRenderer.render(graphics, grid);
  }
}
