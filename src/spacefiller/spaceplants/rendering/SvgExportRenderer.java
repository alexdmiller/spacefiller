package spacefiller.spaceplants.rendering;

import processing.core.PApplet;
import processing.core.PGraphics;
import spacefiller.spaceplants.SPSystem;

import static processing.core.PConstants.*;

public class SvgExportRenderer implements Renderer{
  private PGraphics parentCanvas;
  private PGraphics simCanvas;
  private PGraphics renderCanvas;
  private int backgroundColor;
  private String filename;
  private int framesPerRender;

  public SvgExportRenderer(
      PApplet parent,
      int simWidth,
      int simHeight,
      int backgroundColor,
      String filename,
      int framesPerRender) {
    this.parentCanvas = parent.getGraphics();

    renderCanvas = parent.createGraphics(simWidth, simHeight, SVG, filename);

    this.backgroundColor = backgroundColor;

    this.filename = filename;
    this.framesPerRender = framesPerRender;
  }

  @Override
  public void render(Iterable<SPSystem> systems, int frames) {
    if (frames % framesPerRender == 0) {
      renderCanvas.beginDraw();

      renderCanvas.background(backgroundColor);

      renderCanvas.noStroke();
      renderCanvas.fill(255);

      systems.forEach((system -> {
        system.draw(renderCanvas);
      }));
      renderCanvas.endDraw();

    }
  }
}
