package lusio.scenes;

import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.GraphComponent;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class EdgeScene extends LusioScene {
  SinGraphRenderer sinGraphRenderer;
  PipeGraphRenderer pipeGraphRenderer;
  AnimatedFillGraphRenderer animatedFillGraphRenderer;
  DottedLineGraphRenderer dottedLineGraphRenderer;

  @Override
  public void setup() {
    Graph windowGraph = graphs.get("window");
    Graph poleGraph = graphs.get("pole");
    Graph sideGraph = graphs.get("side");
    Graph smallGraph = graphs.get("small");

    if (windowGraph != null) {
      sinGraphRenderer = new SinGraphRenderer();
      addComponent(new GraphComponent(windowGraph, sinGraphRenderer));
    }

    if (poleGraph != null) {
      pipeGraphRenderer = new PipeGraphRenderer();
      pipeGraphRenderer.setColorProvider(Lusio.instance);
      pipeGraphRenderer.setMaxPerEdge(50);
      addComponent(new GraphComponent(poleGraph, pipeGraphRenderer));

      BasicGraphRenderer basicGraphRenderer = new BasicGraphRenderer(2);
      addComponent(new GraphComponent(poleGraph, basicGraphRenderer));
    }

    if (sideGraph != null) {
      animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
      animatedFillGraphRenderer.setColorProvider(Lusio.instance);
      animatedFillGraphRenderer.setThickness(10);
      addComponent(new GraphComponent(sideGraph, animatedFillGraphRenderer));
    }

    if (smallGraph != null) {
      dottedLineGraphRenderer = new DottedLineGraphRenderer();
      dottedLineGraphRenderer.setThickness(3);
      addComponent(new GraphComponent(smallGraph, dottedLineGraphRenderer));
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    sinGraphRenderer.setSize(cube.getFlipAmount() * 50 + 2);
    sinGraphRenderer.setFreq(cube.getFlipAmount() * 20);
    sinGraphRenderer.setSpeed(cube.getRotationalVelocity() * 0.1f);
    sinGraphRenderer.setColor(cube.getColor());

    pipeGraphRenderer.setDeviation(cube.getFlipAmount() * 10 + 4);
    pipeGraphRenderer.setDotSize(cube.getRotationalVelocity() * 0.1f + 5);
    pipeGraphRenderer.setFreq(100 - cube.getFlipAmount() * 100);
    animatedFillGraphRenderer.setFillSpeed(cube.getRotationalVelocity());

    dottedLineGraphRenderer.setScrollSpeed(cube.getRotationalVelocity() / 5);
    dottedLineGraphRenderer.setColor(cube.getColor());

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
