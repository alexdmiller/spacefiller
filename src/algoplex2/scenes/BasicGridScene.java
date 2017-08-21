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
    animatedFillGraphRenderer.setThickness(2);
    animatedFillGraphRenderer.setFillSpeed(20);
    animatedFillGraphRenderer.setColorProvider(new ConstantColorProvider(0xFFFFFFFF));

    BasicGraphRenderer basicGraphRenderer = new BasicGraphRenderer(2);
    basicGraphRenderer.setColor(0xFFFFFFFF);

    GraphComponent graphComponent = new GraphComponent(grid, dottedLineGraphRenderer);
    addComponent(graphComponent);
  }

  @Override
  public void draw(PGraphics graphics) {
    super.draw(graphics);
  }
}
