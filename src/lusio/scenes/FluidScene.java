package lusio.scenes;

import spacefiller.graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.*;
import processing.core.PGraphics;
import scene.Scene;

import java.util.Map;

public class FluidScene extends LusioScene {
  FluidBoxComponent fluidBoxGenerator;

  @Override
  public void setup() {
    fluidBoxGenerator = new FluidBoxComponent();
    fluidBoxGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    fluidBoxGenerator.setIsoThreshold(3);
    fluidBoxGenerator.setWireFrame(false);
    fluidBoxGenerator.setDrawScale(3);
    addComponent(fluidBoxGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    fluidBoxGenerator.setColor(cube.getColor());
    fluidBoxGenerator.setRotation(cube.getQuaternion());
    fluidBoxGenerator.setIsoThreshold(cube.getEulerRotation()[1] + 4);

    if (cube.getMode() == 1) {
      fluidBoxGenerator.setRestLength(300);
    } else {
      fluidBoxGenerator.setRestLength(cube.getRotationalVelocity() * 10 + 200);
    }

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
