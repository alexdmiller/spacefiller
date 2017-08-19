package lusio.scenes;

import graph.*;
import lightcube.Lightcube;
import lusio.components.GraphComponent;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

/**
 * Created by miller on 8/11/17.
 */
public class NagyLineScene extends LusioScene {
//  BasicGraphRenderer sinGraphRenderer;
  PipeGraphRenderer pipeGraphRenderer;
  AnimatedFillGraphRenderer animatedFillGraphRenderer;
  DottedLineGraphRenderer dottedLineGraphRenderer;
  SinGraphRenderer sinGraphRenderer;

  @Override
  public void setup() {
    Graph nagyGraph = graphs.get("nagy3");

    if (nagyGraph != null) {
      sinGraphRenderer = new SinGraphRenderer();
      sinGraphRenderer.setThickness(6);
      addComponent(new GraphComponent(nagyGraph, sinGraphRenderer));

//      pipeGraphRenderer = new PipeGraphRenderer();
//      pipeGraphRenderer.setDotSize(3);
//      pipeGraphRenderer.setFreq(10);
//      addComponent(new GraphComponent(nagyGraph, pipeGraphRenderer));

//      dottedLineGraphRenderer = new DottedLineGraphRenderer();
//      addComponent(new GraphComponent(nagyGraph, dottedLineGraphRenderer));
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    sinGraphRenderer.setSize(cube.getFlipAmount() * 20);
    // sinGraphRenderer.setFreq(cube.getFlipAmount() * 10);
    // sinGraphRenderer.setSpeed(cube.getRotationalVelocity() * 0.1f);
    sinGraphRenderer.setColor(cube.getColor());
//
//    pipeGraphRenderer.setDeviation(cube.getFlipAmount() * 10 + 4);
//    pipeGraphRenderer.setDotSize(cube.getRotationalVelocity() * 0.1f + 5);
//    pipeGraphRenderer.setFreq(100 - cube.getFlipAmount() * 100);
//    pipeGraphRenderer.setColor(cube.getColor());
//
//    animatedFillGraphRenderer.setFillSpeed(cube.getRotationalVelocity());
//
//    dottedLineGraphRenderer.setScrollSpeed(cube.getRotationalVelocity() / 5);
//    dottedLineGraphRenderer.setColor(cube.getColor());
//    dottedLineGraphRenderer.setColor(cube.getColor());
//    dottedLineGraphRenderer.setSize(cube.getFlipAmount() * 20);

    // sinGraphRenderer.setColor(cube.getColor());

    super.draw(graphics);
  }
}
