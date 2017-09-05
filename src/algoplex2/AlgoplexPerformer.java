package algoplex2;

import algoplex2.scenes.*;
import algoplex2.scenes.ContourScene;
import codeanticode.syphon.SyphonServer;
import common.Integrator;
import common.Integrators;
import graph.BasicGraphRenderer;
import graph.Node;
import modulation.Mod;
import modulation.OscSceneModulator;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PJOGL;
import scene.SceneApplet;

public class AlgoplexPerformer extends SceneApplet {
  public static AlgoplexPerformer instance;
  private static int SPACING = 200;

  public static void main(String[] args) {
    main("algoplex2.AlgoplexPerformer");
  }

  @Mod
  public ContourScene contourScene = new ContourScene();

  @Mod
  public ParticleScene particleScene = new ParticleScene();

  @Mod
  public PsychScene psychScene = new PsychScene();

  @Mod
  public LightScene lightScene = new LightScene();

  public GridScene[] gridScenes = new GridScene[] {
      contourScene,
      particleScene,
      psychScene,
      lightScene
  };

  private Controller controller;
  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;
  private PGraphics transformedCanvas;
  private boolean showUI = true;
  private SyphonServer server;

  private Integrators integrators = new Integrators();
  private Integrator perlinT = integrators.create();
  private Integrator waveTX = integrators.create();
  private Integrator waveTY = integrators.create();

  public AlgoplexPerformer() {
    AlgoplexPerformer.instance = this;
  }

  public void settings() {
    //fullScreen(2);
    size(WIDTH, HEIGHT, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    server = new SyphonServer(this, this.getClass().getName());

    controller = new Controller();

    int cols = WIDTH / SPACING + 2;
    int rows = HEIGHT / SPACING + 2;

    graphTransformer = createGrid(rows, cols, SPACING);

    graphRenderer = new BasicGraphRenderer(1);
    graphRenderer.setColor(0xFFFFFF00);

    addGridScenes(gridScenes);

    transformedCanvas = createGraphics(cols * SPACING, rows * SPACING, P3D);
    canvas = createGraphics(WIDTH, HEIGHT, P3D);

    switchScene(0);

    new OscSceneModulator(this, 9999);
  }

  @Override
  public void draw() {
    this.canvas.beginDraw();
    this.canvas.background(0);
    this.canvas.translate(-SPACING / 2, -SPACING / 2);

    if (currentScene != null) {
      GridScene gridScene = (GridScene) currentScene;
      if (!gridScene.isTransformed()) {
        currentScene.draw(this.canvas);
      }
    }

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

    if (showUI) {
      graphTransformer.drawUI(this.canvas);
    }

    updateGlobalParameters();

    this.canvas.endDraw();

    // getGraphics().image(canvas, 0, 0, WIDTH, HEIGHT);
    server.sendImage(canvas);
  }

  private void updateGlobalParameters() {
    integrators.update();

    float jitterAmount = controller.getValue(16) * 2000;
    perlinT.setSpeed(controller.getValue(17) / 10f);
    float xScale = controller.getValue(18) / 100f;
    float yScale = controller.getValue(19) / 100f;

    float xWaveAmount = controller.getValue(20) * 1000;
    float yWaveAmount = controller.getValue(21) * 1000;
    waveTX.setSpeed(controller.getValue(22) / 10);
    waveTY.setSpeed(controller.getValue(23) / 10);

    for (Node n : graphTransformer.getPostTransformGrid().getNodes()) {
      PVector original = graphTransformer.getPreNode(n).position;

      float noiseX = (float) ((noise(
                original.x * xScale,
                original.y * yScale,
                perlinT.getValue()) - 0.5) * jitterAmount);
      float noiseY = (float) ((noise(
          original.x * xScale,
          original.y * yScale,
          perlinT.getValue() + 100) - 0.5) * jitterAmount);

      float xTheta = waveTX.getValue() + original.x;
      float yTheta = waveTY.getValue() + original.y;

      float circleX = cos(xTheta) * xWaveAmount + cos(yTheta) * yWaveAmount;
      float circleY = sin(xTheta) * xWaveAmount + sin(yTheta) * yWaveAmount;

      n.position.x = original.x + noiseX + circleX;
      n.position.y = original.y + noiseY + circleY;
    }
  }

  @Override
  public void keyPressed() {
    if (key == ' ') {
      showUI = !showUI;
    }

    if (keyCode == RIGHT) {
      gotoNextScene();
    }
  }

  public void addGridScenes(GridScene[] gridScenes) {
    for (GridScene scene : gridScenes) {
      addGridScene(scene);
    }
  }

  public void addGridScene(GridScene gridScene) {
    if (gridScene.isTransformed()) {
      gridScene.setGrid(graphTransformer.getPreTransformGrid());
    } else {
      gridScene.setGrid(graphTransformer.getPostTransformGrid());
    }

    gridScene.setController(controller);

    addScene(gridScene);
  }

  private GraphTransformer createGrid(int rows, int cols, float spacing) {
    rows *= 2;

    rows += 1;
    cols += 1;

    Node[][] nodes = new Node[rows][cols];
    Grid grid = new Grid();

    grid.setCellSize(spacing);

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
        nodes[0][0].copy(),
        nodes[0][cols - 1].copy(),
        nodes[rows - 1][0].copy(),
        nodes[rows - 1][cols - 1].copy()));

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
