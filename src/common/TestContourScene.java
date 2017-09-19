package common;

import algoplex2.Grid;
import common.components.ContourComponent;
import org.junit.jupiter.api.Test;
import particles.Bounds;
import processing.core.PGraphics;
import scene.Scene;
import spacefiller.remote.Mod;
import toxi.geom.Quaternion;

/**
 * Created by miller on 9/17/17.
 */
public class TestContourScene extends Scene {
  @Mod
  public ContourComponent contourComponent;

  @Override
  public void setup() {
    contourComponent = new ContourComponent(
        new Bounds(600, 400, 200));
    contourComponent.setColor(0xFFFFFFFF);
    contourComponent.setPos(0, 0);
    // contourComponent.setRotation(Quaternion.createFromEuler((float) (Math.PI / 2), 0, 0));
    contourComponent.setCellSize(100);
    contourComponent.setNoiseScale(0.5f);
    contourComponent.setLineSize(3);
    contourComponent.setNoiseAmplitude(5000);
    contourComponent.setUpdateSpeed(0.001f);
    contourComponent.setHeightIncrements(50);
    //contourComponent.setXSpeed(0.1f);
    contourComponent.setPos(TestSceneApplet.WIDTH / 2 - 800/2, TestSceneApplet.HEIGHT / 2 - 400/2);
    //contourComponent.setRotation(Quaternion.createFromEuler(0f, 0,  0.1f));

    addComponent(contourComponent);
  }

  @Override
  public void draw(PGraphics graphics) {
    //graphics.ortho();
    graphics.rotateY(0.5f);
    super.draw(graphics);
  }
}
