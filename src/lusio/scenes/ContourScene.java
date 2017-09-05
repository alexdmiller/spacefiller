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
    contourGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    contourGenerator.setRotation(Quaternion.createFromAxisAngle(new Vec3D(-1, 0, 0), 1f));
    contourGenerator.setCellSize(150);
    contourGenerator.setNoiseScale(2);
    contourGenerator.setLineSize(6);
    contourGenerator.setUpdateSpeed(0);

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
    contourGenerator.setNoiseAmplitude(height);
    contourGenerator.setUpdateSpeed(cube.getRotationalVelocity() * 0.0001f);
    contourGenerator.setRotation(Quaternion.createFromEuler(0, euler[1], 0));

    graphics.perspective();
    super.draw(graphics);
    graphics.popMatrix();
  }
}
