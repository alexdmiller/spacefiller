package lusio.scenes;

import common.color.ConstantColorProvider;
import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.GraphComponent;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

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
      animatedFillGraphRenderer.setColorProvider(ConstantColorProvider.WHITE);
      addComponent(new GraphComponent(miller2, animatedFillGraphRenderer));

      dottedLineGraphRenderer = new DottedLineGraphRenderer();
      addComponent(new GraphComponent(miller1, dottedLineGraphRenderer));

      sinGraphRenderer = new SinGraphRenderer();
      addComponent(new GraphComponent(miller3, sinGraphRenderer));
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    sinGraphRenderer.setColor(255);
    sinGraphRenderer.setThickness(2);
    sinGraphRenderer.setFreq(cube.getFlipAmount() * 10 + 1);
    sinGraphRenderer.setSize(cube.getFlipAmount() * 20);

    dottedLineGraphRenderer.setColor(255);
    dottedLineGraphRenderer.setScrollSpeed(cube.getRotationalVelocity() * 5);
    dottedLineGraphRenderer.setSize(cube.getFlipAmount() * 20 + 5);

    animatedFillGraphRenderer.setFillSpeed(cube.getRotationalVelocity() + 0.1f);

    super.draw(graphics);
  }
}
