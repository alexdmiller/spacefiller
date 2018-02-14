package lusio.scenes;

import graph.*;
import graph.renderer.AnimatedFillGraphRenderer;
import graph.renderer.DottedLineGraphRenderer;
import graph.renderer.PipeGraphRenderer;
import graph.renderer.SinGraphRenderer;
import lusio.Lusio;
import lusio.components.GraphComponent;
import processing.core.PGraphics;

public class MillerLineScene extends LusioScene {
  //  BasicGraphRenderer sinGraphRenderer;
  PipeGraphRenderer pipeGraphRenderer;
  AnimatedFillGraphRenderer animatedFillGraphRenderer;
  DottedLineGraphRenderer dottedLineGraphRenderer;
  SinGraphRenderer sinGraphRenderer;

  @Override
  public void setup() {
    Graph miller1 = graphs.get("miller1");
    Graph miller2 = graphs.get("miller2");
    Graph miller3 = graphs.get("miller3");

    if (miller1 != null) {
      animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
      animatedFillGraphRenderer.setColorProvider(Lusio.instance);
      animatedFillGraphRenderer.setThickness(20);
      addComponent(new GraphComponent(miller2, animatedFillGraphRenderer));

      dottedLineGraphRenderer = new DottedLineGraphRenderer();
      dottedLineGraphRenderer.setThickness(5);
      addComponent(new GraphComponent(miller1, dottedLineGraphRenderer));

      sinGraphRenderer = new SinGraphRenderer();
      sinGraphRenderer.setThickness(5);
      addComponent(new GraphComponent(miller3, sinGraphRenderer));
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    if (sinGraphRenderer != null) {
      sinGraphRenderer.setColor(cube.getColor());
      sinGraphRenderer.setThickness(5);
      sinGraphRenderer.setFreq(cube.getCounter() * 10 + 1);
      sinGraphRenderer.setSize(cube.getCounter() * 20);

      dottedLineGraphRenderer.setColor(cube.getColor());
      dottedLineGraphRenderer.setScrollSpeed(cube.getRotationalVelocity() / 10);
      dottedLineGraphRenderer.setSize(cube.getCounter() * 20 + 5);

      animatedFillGraphRenderer.setFillSpeed(cube.getRotationalVelocity() * 4 + 0.1f);
    }
    super.draw(graphics);
  }
}
