package algoplex2.scenes;

import spacefiller.color.ConstantColorProvider;
import lusio.components.PerlinFlowComponent;
import particles.Bounds;
import spacefiller.mapping.Grid;
import spacefiller.remote.Mod;
import processing.core.PGraphics;

public class FlowScene extends GridScene {
  private float t;

  @Mod
  public PerlinFlowComponent perlinFlow;


  @Mod(min = 0, max = 1)
  public float updateSpeed = 0.01f;

  @Override
  public void preSetup(Grid grid) {
    perlinFlow = new PerlinFlowComponent(new Bounds(grid.getWidth() * 2, grid.getHeight() * 2));
    perlinFlow.setColorProvider(ConstantColorProvider.WHITE);
    perlinFlow.setPos(grid.getWidth() / 2 + grid.getCellSize() / 2, grid.getHeight() / 2);
    perlinFlow.setFallSpeed(0);
    perlinFlow.setNoiseSpeed1(0);
    perlinFlow.setNoiseSpeed2(0);
    perlinFlow.setFlowForce(100);
    perlinFlow.setLineLength(100);
    perlinFlow.setSnapToGrid(true);
    perlinFlow.setGridCellSize(grid.getCellSize());
    addComponent(perlinFlow);

    super.preSetup(grid);
  }

  @Override
  public void draw(PGraphics graphics) {
    t += updateSpeed;

    super.draw(graphics);
  }
}
