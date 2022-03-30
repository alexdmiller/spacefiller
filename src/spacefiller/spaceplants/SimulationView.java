package spacefiller.spaceplants;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import spacefiller.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static processing.core.PConstants.*;

public class SimulationView {
  private Simulation simulation;
  private PApplet applet;
  private PGraphics simulationCanvas;
  private PGraphics svgCanvas;
  private boolean snapSvg;
  private boolean snapPng;

  public SimulationView(Simulation simulation, PApplet applet) {
    this.simulation = simulation;
    this.applet = applet;

    simulationCanvas = Utils.createGraphics(simulation.getWidth(), simulation.getHeight(), PConstants.P2D);
    ((PGraphicsOpenGL) simulationCanvas).textureSampling(3);
    simulationCanvas.noSmooth();
  }

  public void snapSvg() {
    snapSvg = true;
  }

  public void snapPng() {
    snapPng = true;
  }

  public void draw() {
    if (snapSvg) {
      svgCanvas = Utils.createGraphics(
          simulation.getWidth(), simulation.getHeight(),
          SVG,
          new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + ".svg");
      svgCanvas.beginDraw();
      svgCanvas.background(0);
      simulation.draw(svgCanvas);
      svgCanvas.endDraw();
      svgCanvas.dispose();
      snapSvg = false;
    } else {
      simulation.preDraw(simulationCanvas);

      simulationCanvas.beginDraw();
      simulationCanvas.noStroke();
      simulationCanvas.fill(0);
      simulationCanvas.rect(0, 0, simulationCanvas.width, simulationCanvas.height);

      simulation.draw(simulationCanvas);
      simulationCanvas.endDraw();

      applet.image(simulationCanvas, 0, 0, applet.width, applet.height);

      if (snapPng) {
        applet.save(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + ".png");
        snapPng = false;
      }
    }
  }
}
