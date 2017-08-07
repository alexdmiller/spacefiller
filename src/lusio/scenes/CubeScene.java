package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;
import toxi.geom.Quaternion;

import java.util.Map;

public class CubeScene extends Scene {
  float cubeSize = 200;
  float padding = 50;
  Quaternion[] history = new Quaternion[50];
  float[] sizeHistory = new float[50];

  @Override
  public void setup(Map<String, Graph> graphs) {
    for (int i = 0; i < history.length; i++) {
      history[i] = new Quaternion();
    }
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    history[0].set(cube.getQuaternion());
    sizeHistory[0] = cube.getFlipAmount() * cube.getFlipAmount() * 0.5f + 0.5f;

    for (int i = history.length - 1; i >= 1; i--) {
      history[i].set(history[i - 1]);
    }

    for (int i = sizeHistory.length - 1; i >= 1; i--) {
      sizeHistory[i] = sizeHistory[i - 1];
    }

    for (int i = 0; i < history.length; i++) {
      graphics.stroke(Lusio.instance.getColor(i));
      graphics.strokeWeight(5);
      graphics.pushMatrix();
      graphics.translate((i * (cubeSize + padding)) % Lusio.WIDTH, ((int) (i * (cubeSize + padding)) / Lusio.WIDTH) * (cubeSize + padding));
      float[] axis = history[i].toAxisAngle();
      graphics.rotate(axis[0], -axis[1], axis[3], axis[2]);
      graphics.box(sizeHistory[i] * cubeSize);
      graphics.popMatrix();
    }

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
