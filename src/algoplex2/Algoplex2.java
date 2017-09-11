package algoplex2;

import algoplex2.scenes.*;
import graph.BasicGraphRenderer;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.Scene;
import scene.SceneApplet;
import spacefiller.remote.MidiRemoteControl;

import java.io.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 5;
  private static int COLS = 8;
  private static int SPACING = 150;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private MidiRemoteControl remote;
  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;
  private PGraphics transformedCanvas;
  private boolean showUI = false;
  private float t;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    //fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    loadGraphs();

    if (graphTransformer == null) {
      graphTransformer = GridUtils.createGrid(ROWS, COLS, SPACING);
    }

    graphRenderer = new BasicGraphRenderer(1);
    graphRenderer.setColor(0xFFFFFF00);

    TriangleScene triangleScene = new TriangleScene();
    addGridScene(triangleScene);

//    ContourScene contourScene = new ContourScene();
//    addGridScene(contourScene);

    PyramidScene pyramidScene = new PyramidScene();
    addGridScene(pyramidScene);

//    ParticleScene particleScene = new ParticleScene();
//    addGridScene(particleScene);

    PsychScene psychScene = new PsychScene();
    addGridScene(psychScene);

    BasicGridFitScene basicGridFitScene = new BasicGridFitScene();
    addGridScene(basicGridFitScene);

    transformedCanvas = createGraphics(COLS * SPACING, ROWS * SPACING, P3D);

    setCanvas(getGraphics());
    setupRemote();

    super.setup();
  }

  private void setupRemote() {
    remote = new MidiRemoteControl("Launch Control XL 8");
    for (Scene scene : scenes) {
      remote.register(scene);
    }

    remote.controller(13).smooth(0.2f).patchTo(remote.target("/TriangleScene/xMod"));
    remote.controller(14).smooth(0.2f).patchTo(remote.target("/TriangleScene/yMod"));
    remote.controller(15).smooth(0.1f).patchTo(remote.target("/TriangleScene/triangleMod"));
    remote.controller(16).smooth(0.05f).patchTo(remote.target("/TriangleScene/shiftAmount"));
    remote.controller(17).patchTo(remote.target("/TriangleScene/speed"));
  }

  @Override
  public void draw() {
    remote.update();

    t += 0.01f;
    this.canvas.background(0);

    if (currentScene != null) {
      GridScene gridScene = (GridScene) currentScene;
      if (!gridScene.isTransformed()) {
        currentScene.draw(this.canvas);
      }
    }

    //graphRenderer.render(getGraphics(), grid);

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

//    for (Node n : graphTransformer.getPostTransformGrid().getNodes()) {
//      PVector original = graphTransformer.getPreNode(n).position;
//      n.position.x = (float) (original.x + (noise(original.x, 0, t) - 0.5) * controller.getValue(0) * 1000);
//      n.position.y = (float) (original.y + (noise(original.y, 1, t) - 0.5) * controller.getValue(0) * 1000);
//    }
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
}
