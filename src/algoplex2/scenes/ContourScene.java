package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Grid;
import common.color.ConstantColorProvider;
import common.components.ContourComponent;
import graph.*;
import lusio.Lusio;
import spacefiller.remote.Mod;
import particles.Bounds;
import processing.core.PGraphics;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

public class ContourScene extends GridScene {
  @Mod
  public ContourComponent contourComponent;

  @Override
  public void preSetup(Grid grid) {
    super.preSetup(grid);

    System.out.println(grid.getWidth());
    System.out.println(grid.getHeight());
    contourComponent = new ContourComponent(
        new Bounds(grid.getWidth(), grid.getHeight()));
    contourComponent.setColor(0xFFFFFFFF);
    contourComponent.setPos(0, 0);
    // contourComponent.setRotation(Quaternion.createFromEuler((float) (Math.PI / 2), 0, 0));
    //contourComponent.setCellSize(grid.getCellSize() * 2);
    contourComponent.setNoiseScale(0.5f);
    contourComponent.setLineSize(3);
    contourComponent.setNoiseAmplitude(5000);
    contourComponent.setUpdateSpeed(0.001f);
    //contourComponent.setHeightIncrements(2);

    addComponent(contourComponent);
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.ortho();
//    contourComponent.setColor(0xffffffff);
//    contourComponent.setNoiseAmplitude(controller.getValue(0) * 5000);
//    contourComponent.setNoiseScale(controller.getValue(1) * 10 + 0.01f);
//    contourComponent.setUpdateSpeed(controller.getValue(2) / 10f);
//    contourComponent.setXSpeed(controller.getValue(3) / 10f);


    super.draw(graphics);
  }
}
