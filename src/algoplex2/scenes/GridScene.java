package algoplex2.scenes;

import algoplex2.Controller;
import scene.Scene;
import spacefiller.mapping.Grid;
import spacefiller.remote.SerialStringRemoteControl;

/**
 * Created by miller on 8/22/17.
 */
public class GridScene extends Scene {
  protected Grid grid;
  protected Controller controller;

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
}
