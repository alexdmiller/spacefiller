package algoplex2.scenes;

import algoplex2.Grid;
import common.components.ContourComponent;
import spacefiller.remote.Mod;
import particles.Bounds;
import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.geom.Quaternion;

public class ContourScene extends GridScene {
  @Mod
  public ContourComponent contourComponent;

  private boolean ortho = false;

  @Override
  public void preSetup(Grid grid) {
    super.preSetup(grid);

    contourComponent = new ContourComponent(
        new Bounds(grid.getWidth(), grid.getHeight(), 500));
    contourComponent.resolution = grid.getColumns();

    contourComponent.setColor(0x00FFFFFF);
    contourComponent.setPos(grid.getWidth() / 2, grid.getHeight() / 2);
    // contourComponent.setRotation(Quaternion.createFromEuler((float) (Math.PI / 2), 0, 0));
    //contourComponent.setCellSize(grid.getCellSize() * 2);
    contourComponent.setNoiseScale(20);
    contourComponent.setLineSize(2);
    contourComponent.setNoiseAmplitude(5);
    contourComponent.slices = 20;
    ortho = true;
    //contourComponent.setHeightIncrements(2);

    setColor(0);

    addComponent(contourComponent);
  }

  @Mod(min = 0.5f, max = 4f)
  public void setResolution(float resolution) {
    contourComponent.resolution = (int) (grid.getColumns() / resolution) * grid.getColumns();
  }

  @Mod(min = 0, max = (float) Math.PI / 2)
  public void setRotation(float angle) {
    contourComponent.setRotation(Quaternion.createFromEuler(0, 0, angle));
    ortho = angle <= 0.1f;
  }

  @Mod(min = 0, max = 1)
  public void setColor(float theta) {
    ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(theta);
    contourComponent.setColor(color1.toARGB());
  }

  @Override
  public void draw(PGraphics graphics) {
    if (ortho) {
      graphics.ortho();
    } else {
      graphics.perspective();
    }

//    contourComponent.setColor(0xffffffff);
//    contourComponent.setNoiseAmplitude(controller.getValue(0) * 5000);
//    contourComponent.setNoiseScale(controller.getValue(1) * 10 + 0.01f);
//    contourComponent.setUpdateSpeed(controller.getValue(2) / 10f);
//    contourComponent.setXSpeed(controller.getValue(3) / 10f);


    super.draw(graphics);
  }
}
