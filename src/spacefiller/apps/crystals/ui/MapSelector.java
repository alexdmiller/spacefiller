package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Animator;
import spacefiller.crystals.engine.Engine;
import processing.core.*;

import static spacefiller.apps.crystals.ui.Utils.makePreviews;

public class MapSelector extends Component {
  private Engine engine;
  private int width;
  private int cols = 7;
  private int rows = 7;
  private PImage[] mapPreviews;

  public MapSelector(PApplet applet, Engine engine, int width) {
    super(applet);
    this.engine = engine;
    this.width = width;

    mapPreviews = makePreviews(engine.getMapCanvas(), engine.getMaps());
  }

  @Override
  protected void doDraw() {
    int imageWidth = width / cols;
    int imageHeight = (int) (imageWidth * (1080f / 1920));

    applet.rectMode(PConstants.CORNER);

    for (int i = 0; i < mapPreviews.length; i++) {
      applet.pushMatrix();
      applet.translate(i % cols * imageWidth,
          i / cols * imageHeight);
      applet.image(mapPreviews[i],
          0,
          0,
          imageWidth,
          imageHeight);
      applet.stroke(100);

      if (engine.getMaps()[i] instanceof Animator.DoubleAnimator) {
        applet.textAlign(PConstants.LEFT, PConstants.TOP);
        applet.text("s", 1, 1);
      }

      applet.noFill();
      applet.rect(0, 0,
          imageWidth,
          imageHeight);
      applet.popMatrix();
    }

    applet.stroke(255);
    applet.noFill();
    applet.rect(engine.getMapIndex() % cols * imageWidth,
        engine.getMapIndex() / cols * imageHeight,
        imageWidth,
        imageHeight);
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    return (int) (getWidth() * ((float) 1080 / 1920));
  }

  @Override
  public void mousePressed(PVector mouse) {
    super.mousePressed(mouse);

    int imageWidth = width / cols;
    int imageHeight = (int) (imageWidth * (1080f / 1920));

    int mapIndex = (int) (mouse.y / imageHeight) * cols + (int) (mouse.x / imageWidth);

    if (mapIndex < engine.getMaps().length) {
      engine.setMapIndex(mapIndex);
    }
  }
}
