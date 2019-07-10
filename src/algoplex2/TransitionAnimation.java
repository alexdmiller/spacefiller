package algoplex2;

import spacefiller.graph.renderer.CrosshairGraphRenderer;
import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.mapping.Grid;

/**
 * Created by miller on 10/4/17.
 */
public class TransitionAnimation {
  private float t;
  private static final int MID_POINT = 100;

  CrosshairGraphRenderer crosshairGraphRenderer;

  public TransitionAnimation() {
    crosshairGraphRenderer = new CrosshairGraphRenderer();
  }

  public void reset() {
    t = 0;
    crosshairGraphRenderer.size = 100 - t;
  }

  public void draw(PGraphics graphics, Grid grid) {
    t++;

    graphics.colorMode(PConstants.RGB);
    if (t < MID_POINT * 2) {
      int c = graphics.color(200, t < MID_POINT ? t * 2 : MID_POINT * 2 - t);
      crosshairGraphRenderer.setColor(c);
      crosshairGraphRenderer.size = 80 - t * 0.3f;
      crosshairGraphRenderer.thickness = 2;
      crosshairGraphRenderer.render(graphics, grid);
    }
  }
}
