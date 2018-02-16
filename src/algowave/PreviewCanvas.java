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
    pGraphics.stroke(100);
    pGraphics.strokeWeight(2);
    pGraphics.noFill();
    pGraphics.rect(1, 1, width - 1, width / pGraphics.width * pGraphics.height - 1);
    pGraphics.noStroke();
  }
}
