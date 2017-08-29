package algoplex2;

import algoplex2.scenes.BasicGridFitScene;
import algoplex2.scenes.GridScene;
import algoplex2.scenes.PsychScene;
import graph.BasicGraphRenderer;
import graph.Node;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.SceneApplet;
import deadpixel.keystone.*;

import java.io.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 4;
  private static int COLS = 6;
  private static int SPACING = 50;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;
  private PGraphics transformedCanvas;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    fullScreen(1);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    loadGraphs();

    if (graphTransformer == null) {
      graphTransformer = createGrid(ROWS, COLS, SPACING);
    }

    graphRenderer = new BasicGraphRenderer(1);
    graphRenderer.setColor(0xFFFFFF00);

//    BasicGridScene gridScene = new BasicGridScene();
//    addGridScene(gridScene);

//    PsychScene psychScene = new PsychScene();
//    addGridScene(psychScene);

    BasicGridFitScene basicGridFitScene = new BasicGridFitScene();
    addGridScene(basicGridFitScene);

    transformedCanvas = createGraphics(COLS * SPACING, ROWS * SPACING, P3D);

    super.setup();
  }

  @Override
  public void draw() {
    this.canvas.background(0);

    if (currentScene != null) {
      GridScene gridScene = (GridScene) currentScene;
      if (!gridScene.isTransformed()) {
        currentScene.draw(this.canvas);
      }
    }


    //graphRenderer.render(getGraphics(), grid);
    graphTransformer.draw(this.canvas);

    this.transformedCanvas.beginDraw();
    this.transformedCanvas.background(0);
    if (currentScene != null) {
      GridScene gridScene = (GridScene) currentScene;
      if (gridScene.isTransformed()) {
        currentScene.draw(this.transformedCanvas);
      }
    }
    this.transformedCanvas.endDraw();

    graphTransformer.drawImage(this.canvas, this.transformedCanvas);

  }

  @Override
  public void mousePressed() {
    graphTransformer.mouseDown(mouseX, mouseY);
  }

  @Override
  public void mouseReleased() {
    graphTransformer.mouseUp(mouseX, mouseY);
    saveGraphs();
  }

  @Override
  public void mouseDragged() {
    graphTransformer.mouseDragged(mouseX, mouseY);
  }

  public void addGridScene(GridScene gridScene) {
    if (gridScene.isTransformed()) {
      gridScene.setGrid(graphTransformer.getPreTransformGrid());
    } else {
      gridScene.setGrid(graphTransformer.getPostTransformGrid());
    }

    addScene(gridScene);
  }

  private void saveGraphs() {
    try {
      FileOutputStream fileOut =
          new FileOutputStream("algoplex2.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(graphTransformer);
      out.close();
      fileOut.close();
    } catch (IOException i) {
      i.printStackTrace();
    }
  }

  private void loadGraphs() {
    try {
      FileInputStream fileIn = new FileInputStream("algoplex2.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      graphTransformer = (GraphTransformer) in.readObject();
      in.close();
      fileIn.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException i) {
      i.printStackTrace();
    } catch (ClassNotFoundException c) {
      c.printStackTrace();
    }
  }


  private GraphTransformer createGrid(int rows, int cols, float spacing) {
    rows *= 2;

    rows += 1;
    cols += 1;

    Node[][] nodes = new Node[rows][cols];
    Grid grid = new Grid();

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

    grid.setBoundingQuad(new Quad(
        nodes[0][0].position.copy(),
        nodes[0][cols - 1].position.copy(),
        nodes[rows - 1][0].position.copy(),
        nodes[rows - 1][cols - 1].position.copy()));

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

        if (col < cols - 1 && row < nodes.length - 1 && nodes[row + 1][col] != null) {
          // top triangle
          grid.addTriangle(nodes[row][col], nodes[row][col + 1], nodes[row + 1][col]);
        }

        if (col < cols - 1 && row < rows - 2) {
          // bottom triangle
          grid.addTriangle(nodes[row + 2][col], nodes[row + 1][col], nodes[row + 2][col + 1]);
        }

        if (col < cols - 1 && row < rows - 2 && nodes[row + 1][col] != null) {
          // right triangle
          grid.addTriangle(nodes[row][col + 1], nodes[row + 2][col + 1], nodes[row + 1][col]);
        }

        if (row < rows - 2 && nodes[row + 1][col] != null) {
          // left triangle
          grid.addTriangle(nodes[row][col], nodes[row + 2][col], nodes[row + 1][col]);
        }

        if (row < rows - 2 && col < cols - 1) {
          grid.addSquare(nodes[row][col], nodes[row][col + 1], nodes[row + 2][col + 1], nodes[row + 2][col], nodes[row + 1][col]);
        }
      }
    }

    return new GraphTransformer(grid);
  }
}
