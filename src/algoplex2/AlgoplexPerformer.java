package algoplex2;

import algoplex2.scenes.*;
import algoplex2.scenes.ContourScene;
import codeanticode.syphon.SyphonServer;
import graph.GridUtils;
import common.Integrator;
import common.Integrators;
import graph.Node;
import graph.SinGraphRenderer;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PJOGL;
import scene.SceneApplet;
import spacefiller.remote.VDMXWriter;

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
  public TriangleScene triangleScene = new TriangleScene();

  public GridScene[] gridScenes = new GridScene[] {
      contourScene,
      particleScene,
      // psychScene,
      triangleScene
  };

  private GraphTransformer graphTransformer;

  @Mod
  public SinGraphRenderer graphRenderer;

  private PGraphics transformedCanvas;
  private boolean showUI = true;
  private SyphonServer server;

  private Integrators integrators = new Integrators();

  public Integrator perlinT = integrators.create();
  public Integrator waveTX = integrators.create();
  public Integrator waveTY = integrators.create();

  @Mod(min = 0, max = 2000)
  public float jitterAmount = 0;

  @Mod(min = 0, max = 100)
  public float xScale = 20;

  @Mod(min = 0, max = 100)
  public float yScale = 20;

  @Mod(min = 0, max = 1000)
  public float xWaveAmount;

  @Mod(min = 0, max = 1000)
  public float yWaveAmount;

  public AlgoplexPerformer() {
    AlgoplexPerformer.instance = this;
  }

  @Mod(min = 0, max = 0.1f)
  public void setUpdateSpeed(float updateSpeed) {
    perlinT.setSpeed(updateSpeed);
  }

  @Mod(min = 0, max = 0.1f)
  public void setWaveXSpeed(float speed) {
    waveTX.setSpeed(speed);
  }

  @Mod(min = 0, max = 0.1f)
  public void setWaveYSpeed(float speed) {
    waveTY.setSpeed(speed);
  }

  @Mod
  public void toggleUI() {
    this.showUI = !showUI;
  }

  public void settings() {
    fullScreen(2);
    size(WIDTH, HEIGHT, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    server = new SyphonServer(this, this.getClass().getName());

    int cols = WIDTH / SPACING + 2;
    int rows = HEIGHT / SPACING + 2;

      graphTransformer = GridUtils.createGraphTransformer(rows, cols, SPACING);

    //graphRenderer = new BasicGraphRenderer(1);
    graphRenderer = new SinGraphRenderer();
    graphRenderer.setThickness(2);
    graphRenderer.setColor(0xffff5ea6);

    addGridScenes(gridScenes);

    transformedCanvas = createGraphics(cols * SPACING, rows * SPACING, P3D);
    canvas = createGraphics(WIDTH, HEIGHT, P3D);

    switchScene(0);

    perlinT.setSpeed(0.1f);

    OscRemoteControl remote = new OscRemoteControl(this);
    VDMXWriter.exportVDMXJson("algoplex-performer", remote.getTargetMap(), 9998);
    remote.listen(9998);
  }

  @Mod(min = 0, max = 10)
  public void setBorderThickness(float thickness) {
    graphRenderer.setThickness(thickness);
  }

  @Override
  public void draw() {
    this.canvas.beginDraw();
    this.canvas.background(0);
    // this.canvas.translate(-SPACING / 2, -SPACING / 2);

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

    graphRenderer.render(this.canvas, this.graphTransformer.getPostTransformGrid());

    updateGlobalParameters();

    this.canvas.endDraw();

    getGraphics().image(canvas, 0, 0, WIDTH, HEIGHT);
    server.sendImage(canvas);
  }

  private void updateGlobalParameters() {
    integrators.update();

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
      gridScene.preSetup(graphTransformer.getPreTransformGrid());
    } else {
      gridScene.preSetup(graphTransformer.getPostTransformGrid());
    }
    addScene(gridScene);
  }
}
