package algoplex2.scenes;

import algoplex2.Grid;
import graph.Graph;
import scene.Scene;

/**
 * Created by miller on 8/22/17.
 */
public class GridScene extends Scene {
  protected Grid grid;
  private boolean transformed = false;

  public void setGrid(Grid grid) {
    this.grid = grid;
  }

  public void fitToGrid() {
    transformed = true;
  }

  public boolean isTransformed() {
    return transformed;
  }
}
