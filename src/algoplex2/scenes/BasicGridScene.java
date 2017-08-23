package algoplex2.scenes;

import algoplex2.Algoplex2;
import color.ColorProvider;
import color.ConstantColorProvider;
import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.ContourComponent;
import lusio.components.GraphComponent;
import particles.Bounds;
import processing.core.PConstants;
import processing.core.PGraphics;
import scene.Scene;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import java.util.Map;

public class BasicGridScene extends GridScene {
  SinGraphRenderer sinGraphRenderer;
  float t;

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
    // addComponent(new GraphComponent(grid, pipeGraphRenderer));

    GraphComponent graphComponent = new GraphComponent(grid, animatedFillGraphRenderer);
    addComponent(graphComponent);

//    GraphComponent graphComponent = new GraphComponent(grid, sinGraphRenderer);
//    addComponent(graphComponent);

  }

  @Override
  public void draw(PGraphics graphics) {
    t += 0.01f;
    graphics.noStroke();
    int i = 0;
//    for (Node[] triangle : grid.getTriangles()) {
//      i++;
//      graphics.fill(Algoplex2.instance.noise(i + t) * 255, Algoplex2.instance.noise(0, i + t) * 255, Algoplex2.instance.noise(0, 0, i + t) * 255);
//      graphics.triangle(
//          triangle[0].position.x, triangle[0].position.y,
//          triangle[1].position.x, triangle[1].position.y,
//          triangle[2].position.x, triangle[2].position.y);
//    }

    for (Node[] square : grid.getSquares()) {
      i++;
      graphics.fill(Algoplex2.instance.noise(i + t) * 255, Algoplex2.instance.noise(0, i + t) * 255, Algoplex2.instance.noise(0, 0, i + t) * 255);
      graphics.beginShape();
      for (Node node : square) {
        graphics.vertex(node.position.x, node.position.y);
      }
      graphics.endShape(PConstants.CLOSE);
    }
    super.draw(graphics);
  }
}
