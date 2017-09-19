package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import lusio.Lusio;
import processing.core.PGraphics;
import scene.Scene;
import toxi.geom.Quaternion;

import java.util.Map;

public class NestedCubeScene extends LusioScene {
  float cubeSize = 200;
  float padding = 50;
  Quaternion[] history = new Quaternion[20];
  float[] sizeHistory = new float[20];

  @Override
  public void setup() {
    for (int i = 0; i < history.length; i++) {
      history[i] = new Quaternion();
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    history[0].set(cube.getQuaternion());
    sizeHistory[0] = cube.getFlipAmount() * cube.getFlipAmount() * 0.5f + 0.5f;

    for (int i = history.length - 1; i >= 1; i--) {
      history[i].set(history[i - 1]);
    }

    for (int i = sizeHistory.length - 1; i >= 1; i--) {
      sizeHistory[i] = sizeHistory[i - 1];
    }

    graphics.noFill();
    for (int i = 0; i < history.length; i++) {
      graphics.stroke(Lusio.instance.getColor(i));
      graphics.strokeWeight(5);
      graphics.pushMatrix();
      graphics.translate(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
      float[] axis = history[i].toAxisAngle();
      graphics.rotate(axis[0], -axis[1], axis[3], axis[2]);
      graphics.box(sizeHistory[i] * cubeSize + i * padding);
      graphics.popMatrix();
    }

    super.draw(graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
