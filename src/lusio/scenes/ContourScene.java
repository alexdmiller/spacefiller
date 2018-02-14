package lusio.scenes;

import lusio.Lusio;
import common.components.ContourComponent;
import particles.Bounds;
import processing.core.PGraphics;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

public class ContourScene extends LusioScene {
  ContourComponent contourGenerator;
  private float height;

  @Override
  public void setup() {
    contourGenerator = new ContourComponent(new Bounds(2000));
    contourGenerator.resolution = 50;
    contourGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    contourGenerator.setRotation(Quaternion.createFromAxisAngle(new Vec3D(-1, 0, 0), 1f));
    contourGenerator.setLineSize(3);
    contourGenerator.sinHeight = 0;
    contourGenerator.noiseAmplitude = 5;

    addComponent(contourGenerator);
  }

  @Override
  public void draw(PGraphics graphics) {
    if (cube.getFlipAmount() > 0.5 && height < 500) {
      height += cube.getFlipAmount() * 5;
    } else if (height > 20) {
      height--;
    } else if (height <= 20){
      height = 20;
    }

    float euler[] = cube.getEulerRotation();
    graphics.pushMatrix();
    graphics.translate(0, -200, 0);
    contourGenerator.setColor(cube.getColor());
    contourGenerator.setXSpeed(-euler[0] * -0.01f);
    contourGenerator.setYSpeed(euler[2] * -0.01f);
//    contourGenerator.setNoiseAmplitude(10);
    contourGenerator.setUpdateSpeed(cube.getRotationalVelocity() * 0.0001f);
    contourGenerator.setRotation(Quaternion.createFromEuler(0, euler[1], 0));
//
//    contourGenerator.slices = 50;

    contourGenerator.getBounds().setDepth(cube.getCounter() * 2000);
    //contourGenerator.set

    graphics.perspective();
    super.draw(graphics);
    graphics.popMatrix();
  }
}
