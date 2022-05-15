package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import static spacefiller.apps.crystals.ui.Utils.makePreviews;

public class SeedSelector extends Component {
  private Engine engine;
  private int width;
  private int cols = 7;
  private int rows = 7;
  private PImage[] seedPreviews;

  public SeedSelector(PApplet applet, Engine engine, int width) {
    super(applet);
    this.engine = engine;
    this.width = width;

    seedPreviews = makePreviews(engine.getSeedCanvas(), engine.getSeeds());
  }

  @Override
  protected void doDraw() {
    int imageWidth = width / cols;
    int imageHeight = (int) (imageWidth * (1080f / 1920));

    applet.rectMode(PConstants.CORNER);

    for (int i = 0; i < seedPreviews.length; i++) {
      applet.image(seedPreviews[i],
          i % cols * imageWidth,
          i / cols * imageHeight,
          imageWidth,
          imageHeight);
      applet.stroke(100);
      applet.rect(i % cols * imageWidth,
          i / cols * imageHeight,
          imageWidth,
          imageHeight);
    }

    applet.stroke(255);
    applet.noFill();
    applet.rect(engine.getSeedIndex() % cols * imageWidth,
        engine.getSeedIndex() / cols * imageHeight,
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

    if (mapIndex < engine.getSeeds().length) {
      engine.setSeedIndex(mapIndex);
    }
  }
}
