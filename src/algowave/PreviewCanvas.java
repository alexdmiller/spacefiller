package algowave;

import controlP5.Canvas;
import processing.core.PGraphics;
import processing.core.PImage;

public class PreviewCanvas extends Canvas {
  private PImage image;
  private float width;

  public PreviewCanvas(PImage image, float width) {
    this.image = image;
    this.width = width;
  }

  @Override
  public void draw(PGraphics pGraphics) {
    pGraphics.image(image, 0, 0, width, width / pGraphics.width * pGraphics.height);
    pGraphics.stroke(255);
    pGraphics.noFill();
    pGraphics.rect(0, 0, width, width / pGraphics.width * pGraphics.height);
    pGraphics.noStroke();
  }
}
