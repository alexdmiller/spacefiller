package spacefiller.spaceplants.rendering;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import spacefiller.spaceplants.SPSystem;

import static processing.core.PConstants.DISABLE_TEXTURE_MIPMAPS;
import static processing.core.PConstants.P2D;

public class PixelatedRenderer implements Renderer {
  private PGraphics simCanvas;
  private PGraphics renderCanvas;
  private boolean backgroundOn;
  private int backgroundColor;

  public PixelatedRenderer(
      PApplet parent,
      int simWidth,
      int simHeight,
      int renderWidth,
      int renderHeight,
      boolean backgroundOn,
      int backgroundColor) {
    simCanvas = parent.createGraphics(simWidth, simHeight, P2D);
    simCanvas.noSmooth();
    simCanvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL) simCanvas).textureSampling(3);

    renderCanvas = parent.createGraphics(renderWidth, renderHeight, P2D);
    renderCanvas.noSmooth();
    renderCanvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL) renderCanvas).textureSampling(3);

    this.backgroundOn = backgroundOn;
    this.backgroundColor = backgroundColor;
  }

  @Override
  public PImage render(Iterable<SPSystem> systems) {
    simCanvas.beginDraw();
    simCanvas.clear();

    if (backgroundOn) {
      simCanvas.background(backgroundColor);
    }

    simCanvas.noStroke();
    simCanvas.fill(255);

    systems.forEach((system -> {
      system.draw(simCanvas);
    }));
    simCanvas.endDraw();

    renderCanvas.beginDraw();
    renderCanvas.image(simCanvas, 0, 0, renderCanvas.width, renderCanvas.height);
    renderCanvas.endDraw();

    return renderCanvas;
  }
}
