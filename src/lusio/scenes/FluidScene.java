package lusio.scenes;

import graph.Graph;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import processing.core.PGraphics;

import java.util.Map;

public class FluidScene extends Scene {
  FluidBoxGenerator fluidBoxGenerator;

  @Override
  public void setup(Map<String, Graph> graphs) {
    fluidBoxGenerator = new FluidBoxGenerator();
    fluidBoxGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    fluidBoxGenerator.setIsoThreshold(3);
    fluidBoxGenerator.setWireFrame(false);
    fluidBoxGenerator.setDrawScale(3);
    addGenerator(fluidBoxGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    fluidBoxGenerator.setColor(cube.getColor());
    fluidBoxGenerator.setRotation(cube.getQuaternion());
    fluidBoxGenerator.setIsoThreshold(cube.getEulerRotation()[1] + 4);

    if (cube.getMode() == 1) {
      fluidBoxGenerator.setRestLength(400);
    } else {
      fluidBoxGenerator.setRestLength(Math.max(cube.getRotationalVelocity(), 1) * 10 + 200);
    }

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
