package algoplex2.scenes;

import color.ColorProvider;
import color.ConstantColorProvider;
import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.ContourComponent;
import lusio.components.GraphComponent;
import particles.Bounds;
import processing.core.PGraphics;
import scene.Scene;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import java.util.Map;

public class BasicGridScene extends Scene {
  SinGraphRenderer sinGraphRenderer;
  private Graph grid;

  public void setGrid(Graph grid) {
    this.grid = grid;
  }

  @Override
  public void setup() {
    sinGraphRenderer = new SinGraphRenderer();
    sinGraphRenderer.setThickness(2);
    sinGraphRenderer.setColor(0xFFFFFFFF);
    sinGraphRenderer.setFreq(10);

    DottedLineGraphRenderer dottedLineGraphRenderer = new DottedLineGraphRenderer();
    dottedLineGraphRenderer.setThickness(2);
    dottedLineGraphRenderer.setColor(0xFFFFFFFF);

    AnimatedFillGraphRenderer animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
    animatedFillGraphRenderer.setThickness(5);
    animatedFillGraphRenderer.setFillSpeed(30);
    animatedFillGraphRenderer.setColorProvider(new ConstantColorProvider(0xFFFFFFFF));

    BasicGraphRenderer basicGraphRenderer = new BasicGraphRenderer(2);
    basicGraphRenderer.setColor(0x11FFFFFF);

    PipeGraphRenderer pipeGraphRenderer = new PipeGraphRenderer();
    pipeGraphRenderer.setColorProvider(new ConstantColorProvider(0xFFFFFFFF));
    pipeGraphRenderer.setMaxPerEdge(50);
    pipeGraphRenderer.setDotSize(4);
    pipeGraphRenderer.setFreq(10);
    addComponent(new GraphComponent(grid, pipeGraphRenderer));

    GraphComponent graphComponent = new GraphComponent(grid, basicGraphRenderer);
    addComponent(graphComponent);

//    GraphComponent graphComponent = new GraphComponent(grid, sinGraphRenderer);
//    addComponent(graphComponent);

  }

  @Override
  public void draw(PGraphics graphics) {
    super.draw(graphics);
  }
}
