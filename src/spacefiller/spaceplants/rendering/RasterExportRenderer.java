package spacefiller.spaceplants.rendering;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import spacefiller.spaceplants.SPSystem;

import static processing.core.PConstants.DISABLE_TEXTURE_MIPMAPS;
import static processing.core.PConstants.P2D;

public class RasterExportRenderer implements Renderer {
  private PGraphics parentCanvas;
  private PGraphics simCanvas;
  private PGraphics renderCanvas;
  private int backgroundColor;
  private String filename;
  private int framesPerRender;

  public RasterExportRenderer(
      PApplet parent,
      int simWidth,
      int simHeight,
      int renderWidth,
      int renderHeight,
      int backgroundColor,
      String filename,
      int framesPerRender) {
    this.parentCanvas = parent.getGraphics();

    simCanvas = parent.createGraphics(simWidth, simHeight, P2D);
    simCanvas.noSmooth();
    simCanvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL) simCanvas).textureSampling(3);

    renderCanvas = parent.createGraphics(renderWidth, renderHeight, P2D);
    renderCanvas.noSmooth();
    renderCanvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL) renderCanvas).textureSampling(3);

    simCanvas.beginDraw();
    simCanvas.clear();
    simCanvas.endDraw();

    renderCanvas.beginDraw();
    renderCanvas.endDraw();

    this.backgroundColor = backgroundColor;

    this.filename = filename;
    this.framesPerRender = framesPerRender;
  }

  @Override
  public void render(Iterable<SPSystem> systems, int frames) {
    if (frames % framesPerRender == 0) {
      simCanvas.beginDraw();
      simCanvas.clear();

      simCanvas.background(backgroundColor);

      simCanvas.noStroke();
      simCanvas.fill(255);

      systems.forEach((system -> {
        system.draw(simCanvas);
      }));
      simCanvas.endDraw();

      renderCanvas.beginDraw();
      renderCanvas.image(simCanvas, 0, 0, renderCanvas.width, renderCanvas.height);
      renderCanvas.endDraw();

      System.out.println(String.format(filename, frames));
      renderCanvas.save(String.format(filename, frames));
    }
  }
}
