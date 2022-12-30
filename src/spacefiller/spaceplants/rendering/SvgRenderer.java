package spacefiller.spaceplants.rendering;

import processing.core.PApplet;
import processing.core.PGraphics;
import spacefiller.spaceplants.SPSystem;

import static processing.core.PConstants.*;

public class SvgRenderer implements Renderer{
  private PGraphics parentCanvas;
  private PGraphics simCanvas;
  private PGraphics renderCanvas;
  private int backgroundColor;
  private String filename;

  public SvgRenderer(
      PApplet parent,
      int simWidth,
      int simHeight,
      int backgroundColor,
      String filename) {
    this.parentCanvas = parent.getGraphics();

    renderCanvas = parent.createGraphics(simWidth, simHeight, SVG);

    this.backgroundColor = backgroundColor;

    this.filename = filename;
  }

  @Override
  public void render(Iterable<SPSystem> systems) {
    renderCanvas.beginDraw();
    renderCanvas.clear();

    renderCanvas.background(backgroundColor);

    renderCanvas.noStroke();
    renderCanvas.fill(255);

    systems.forEach((system -> {
      system.draw(renderCanvas);
    }));
    renderCanvas.endDraw();

    renderCanvas.save(filename);
  }
}
