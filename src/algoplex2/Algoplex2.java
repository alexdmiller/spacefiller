package algoplex2;

import algoplex2.scenes.BasicGridScene;
import graph.Graph;
import graph.Node;
import processing.core.PApplet;
import processing.opengl.PJOGL;
import scene.Scene;
import scene.SceneApplet;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    Graph grid = createGrid(10, 10, 100);

    BasicGridScene gridScene = new BasicGridScene();
    gridScene.setGrid(grid);
    addScene(gridScene);

    super.setup();
  }

  private Graph createGrid(int rows, int cols, float spacing) {
    Node[][] nodes = new Node[rows][cols];
    Graph grid = new Graph();
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        nodes[row][col] = grid.createNode(col * spacing, row * spacing);
      }
    }

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (row < rows - 1) {
          if (col > 0) {
            // south west
            grid.createEdge(nodes[row][col], nodes[row + 1][col - 1]);
          }

          // south
          grid.createEdge(nodes[row][col], nodes[row + 1][col]);

          // south east
          if (col < cols - 1) {
            grid.createEdge(nodes[row][col], nodes[row + 1][col + 1]);
          }
        }

        // east
        if (col < cols - 1) {
          grid.createEdge(nodes[row][col], nodes[row][col + 1]);
        }
      }
    }

    return grid;
  }
}
