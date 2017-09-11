package algoplex2.scenes;

import algoplex2.Controller;
import algoplex2.Grid;
import graph.Graph;
import scene.Scene;

/**
 * Created by miller on 8/22/17.
 */
public class GridScene extends Scene {
  protected Grid grid;
  protected Controller controller;
  private boolean transformed = true;

  public void preSetup(Grid grid) {
    setGrid(grid);
  }

  @Override
  public void teardown() {
    // Don't clear components
  }


  public void setGrid(Grid grid) {
    this.grid = grid;
  }

  public void setController(Controller controller) {
    this.controller = controller;
  }

  public void fitToGrid() {
    transformed = true;
  }

  public boolean isTransformed() {
    return transformed;
  }
}
