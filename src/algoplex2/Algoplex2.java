package algoplex2;

import algoplex2.scenes.BasicGridScene;
import graph.BasicGraphRenderer;
import graph.Graph;
import graph.Node;
import megamu.mesh.Delaunay;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;
import scene.Scene;
import scene.SceneApplet;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private Graph grid;
  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;

  private static int ROWS = 3;
  private static int COLS = 3;
  private static int SPACING = 50;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    grid = createGrid(ROWS, COLS, SPACING);

    graphRenderer = new BasicGraphRenderer(1);
    graphRenderer.setColor(0xFFFFFF00);

    graphTransformer = new GraphTransformer(grid, new Quad(
        new PVector(0, 0),
        new PVector(COLS * SPACING, 0),
        new PVector(COLS * SPACING, ROWS * SPACING),
        new PVector(0, ROWS * SPACING)));

//    BasicGridScene gridScene = new BasicGridScene();
//    gridScene.setGrid(grid);
//    addScene(gridScene);

    super.setup();
  }

  private Graph createGrid(int rows, int cols, float spacing) {
    rows *= 2;

    rows += 1;
    cols += 1;

    Node[][] nodes = new Node[rows][cols];
    Graph grid = new Graph();

    for (int row = 0; row < rows; row += 2) {
      float yPos = row/2 * spacing;
      for (int col = 0; col < cols; col++) {
        nodes[row][col] = grid.createNode(col * spacing, yPos);
      }

      if (row < rows - 2) {
        for (int col = 0; col < cols - 1; col++) {
          nodes[row + 1][col] = grid.createNode(col * spacing + spacing / 2, yPos + spacing / 2);
        }
      }
    }

    for (int row = 0; row < rows; row += 2) {
      for (int col = 0; col < cols; col++) {
        // top left to top right
        if (col < cols - 1) {
          grid.createEdge(nodes[row][col], nodes[row][col + 1]);
        }

        // top left to bottom left
        if (row < rows - 2) {
          grid.createEdge(nodes[row][col], nodes[row + 2][col]);
        }

        if (row < nodes.length - 1 && nodes[row + 1][col] != null) {
          // middle to top left
          grid.createEdge(nodes[row + 1][col], nodes[row][col]);

          // middle to top right
          if (col < cols - 1) {
            grid.createEdge(nodes[row + 1][col], nodes[row][col + 1]);
          }

          // middle to bottom left
          if (row < rows - 2) {
            grid.createEdge(nodes[row + 1][col], nodes[row + 2][col]);
          }

          // middle to bottom right
          if (row < rows - 2 && col < cols - 1) {
            grid.createEdge(nodes[row + 1][col], nodes[row + 2][col + 1]);
          }
        }
      }
    }

//    for (int row = 0; row < rows; row++) {
//      for (int col = 0; col < cols; col++) {
//        if (row < rows - 1) {
//          if (col > 0) {
//            // south west
//            grid.createEdge(nodes[row][col], nodes[row + 1][col - 1]);
//          }
//
//          // south
//          grid.createEdge(nodes[row][col], nodes[row + 1][col]);
//
//          // south east
//          if (col < cols - 1) {
//            grid.createEdge(nodes[row][col], nodes[row + 1][col + 1]);
//          }
//        }
//
//        // east
//        if (col < cols - 1) {
//          grid.createEdge(nodes[row][col], nodes[row][col + 1]);
//        }
//      }
//    }

    return grid;
  }

  private Graph createDenseGrid(int rows, int cols, float spacing) {
    Graph grid = new Graph();

    float[][] points = new float[(rows * 2) * cols][2];
    Node[] nodes = new Node[(rows * 2) * cols];

    int i = 0;
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        points[i][0] = col * spacing;
        points[i][1] = row * spacing;
        nodes[i] = grid.createNode(points[i][0], points[i][1]);
        i++;

        points[i][0] = col * spacing + spacing / 2;
        points[i][1] = row * spacing + spacing / 2;
        nodes[i] = grid.createNode(points[i][0], points[i][1]);
        i++;
      }
    }
    Delaunay delaunay = new Delaunay(points);
    float[][] myEdges = delaunay.getEdges();

    int[][] links = delaunay.getLinks();

    for (int j = 0; j < links.length; j++) {
      grid.createEdge(nodes[links[j][0]], nodes[links[j][1]]);
    }

    return grid;
  }

  @Override
  public void draw() {
    super.draw();

    graphRenderer.render(getGraphics(), grid);
    graphTransformer.draw(getGraphics());
  }

  @Override
  public void mousePressed() {
    graphTransformer.mouseDown(mouseX, mouseY);
  }

  @Override
  public void mouseReleased() {
    graphTransformer.mouseUp(mouseX, mouseY);
  }

  @Override
  public void mouseDragged() {
    graphTransformer.mouseDragged(mouseX, mouseY);
  }

}
