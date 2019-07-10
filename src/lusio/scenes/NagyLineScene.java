package lusio.scenes;

import spacefiller.graph.*;
import spacefiller.graph.renderer.AnimatedFillGraphRenderer;
import spacefiller.graph.renderer.DottedLineGraphRenderer;
import spacefiller.graph.renderer.PipeGraphRenderer;
import spacefiller.graph.renderer.SinGraphRenderer;
import lusio.Lusio;
import lusio.components.GraphComponent;
import processing.core.PGraphics;

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
    Graph nagy1 = graphs.get("nagy1");
    Graph nagy2 = graphs.get("nagy2");
    Graph nagy3 = graphs.get("nagy3");

    if (nagy1 != null) {
      sinGraphRenderer = new SinGraphRenderer();
      sinGraphRenderer.setThickness(2);
      //addComponent(new GraphComponent(nagy1, sinGraphRenderer));

//      pipeGraphRenderer = new PipeGraphRenderer();
//      pipeGraphRenderer.setDotSize(3);
//      pipeGraphRenderer.setFreq(10);
//      pipeGraphRenderer.setMaxPerEdge(100);
      //addComponent(new GraphComponent(nagy1, pipeGraphRenderer));

      dottedLineGraphRenderer = new DottedLineGraphRenderer();
      addComponent(new GraphComponent(nagy2, dottedLineGraphRenderer));

      addComponent(new GraphComponent(nagy1, dottedLineGraphRenderer));
      addComponent(new GraphComponent(nagy3, dottedLineGraphRenderer));

      animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
      animatedFillGraphRenderer.setColorProvider(Lusio.instance);
      //addComponent(new GraphComponent(nagy1, animatedFillGraphRenderer));
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    dottedLineGraphRenderer.setColor(cube.getColor());
    dottedLineGraphRenderer.setSize(20 - cube.getFlipAmount() * 19);
    dottedLineGraphRenderer.setSpacing(cube.getFlipAmount() * 100 + 200);
    dottedLineGraphRenderer.setThickness(cube.getFlipAmount() * 200 + 2);
    dottedLineGraphRenderer.setScrollSpeed(cube.getRotationalVelocity() * 1);
    //dottedLineGraphRenderer.setSpacing(cube.getRotationalVelocity() * 20 + 50);
    //dottedLineGraphRenderer.setScrollSpeed(0.1f);
    //dottedLineGraphRenderer.setScrollSpeed(cube.getFlipAmount() * 2);
//    pipeGraphRenderer.setColorProvider(ConstantColorProvider.WHITE);
//    pipeGraphRenderer.setDeviation(cube.getFlipAmount() * 100);
//    if (sinGraphRenderer != null) {
//      sinGraphRenderer.setSize(cube.getFlipAmount() * 20);
//      sinGraphRenderer.setFreq(cube.getFlipAmount() * 20);
////      // sinGraphRenderer.setFreq(cube.getFlipAmount() * 10);
////      // sinGraphRenderer.setSpeed(cube.getRotationalVelocity() * 0.1f);
//      sinGraphRenderer.setColor(cube.getColor());
////
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
    //}
    super.draw(graphics);
  }
}
