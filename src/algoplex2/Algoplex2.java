package algoplex2;

import algoplex2.scenes.*;
import graph.GridUtils;
import graph.BasicGraphRenderer;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.Scene;
import scene.SceneApplet;
import spacefiller.remote.MidiRemoteControl;

import java.io.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 4;
  private static int COLS = 6;
  private static int SPACING = 150;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private MidiRemoteControl remote;
  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;
  private PGraphics transformedCanvas;
  private boolean showUI = false;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    fullScreen(1);
    size(1920, 1080, P3D);
    PJOGL.profile = 2;
  }

  public final void setup() {
    loadGraphs();

    if (graphTransformer == null) {
      graphTransformer = GridUtils.createGraphTransformer(ROWS, COLS, SPACING);
    }

    graphRenderer = new BasicGraphRenderer(1);
    graphRenderer.setColor(0xFFFFFF00);

    GridScene[] gridScenes = new GridScene[] {
        new PerlinTriangles(),
        new PerlinGrid(),
        new CrossScene(),
        new ColorBath(),
        new VeinScene(),
        new WormScene(),
        new ShiftingEdgeScene(),
        new GradientTriangleScene(),
        new TriangleScene(),
        new FlowScene(),
        new ContourScene(),
        new PyramidScene(),
        new ParticleScene(),
        new PsychScene()
    };

    for (GridScene scene : gridScenes) {
      addGridScene(scene);
    }

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

    remote.register(this);

    remote.printAddresses();

    remote.controller(13).smooth(0.05f).send(remote.target("/CrossScene/color1Rotation"));
    remote.controller(14).smooth(0.05f).send(remote.target("/CrossScene/color2Rotation"));
    remote.controller(15).smooth(0.05f).send(remote.target("/CrossScene/color3Rotation"));
    remote.controller(16).smooth(0.05f).send(remote.target("/CrossScene/color4Rotation"));
    remote.controller(17).smooth(0.15f).send(remote.target("/CrossScene/crossSize"));
    remote.controller(18).smooth(0.15f).send(remote.target("/CrossScene/xSize"));
    remote.controller(19).smooth(0.15f).invert().send(remote.target("/CrossScene/rotation"));

    remote.controller(13).smooth(0.05f).send(remote.target("/ColorBath/color1Rotation"));
    remote.controller(14).smooth(0.05f).send(remote.target("/ColorBath/color2Rotation"));
    remote.controller(15).smooth(0.05f).send(remote.target("/ColorBath/color3Rotation"));
    remote.controller(16).smooth(0.05f).send(remote.target("/ColorBath/color4Rotation"));

    remote.controller(13).send(remote.target("/VeinScene/addStuff"));
    remote.controller(14).send(remote.target("/VeinScene/treeComponent/pulsePeriod"));
    remote.controller(15).send(remote.target("/VeinScene/treeComponent/growthSpeed"));
    remote.controller(16).send(remote.target("/VeinScene/treeComponent/attractorInfluenceRadius"));

    /*
    /WormScene/flockParticles/alignmentThreshold
/WormScene/flockParticles/alignmentWeight
/WormScene/flockParticles/cohesionThreshold
/WormScene/flockParticles/cohesionWeight
/WormScene/flockParticles/desiredSeparation
/WormScene/flockParticles/maxSpeed
/WormScene/flockParticles/separationWeight
/WormScene/followPaths/maxForce
/WormScene/followPaths/maxSpeed
/WormScene/followPaths/radius
/WormScene/particleWebRenderer/lineSize
/WormScene/particleWebRenderer/lineThreshold
/WormScene/repelFixedPoints/repelStrength
/WormScene/repelFixedPoints/repelThreshold
     */
    remote.controller(13).send(remote.target("/WormScene/flockParticles/desiredSeparation"));
    remote.controller(14).send(remote.target("/WormScene/flockParticles/maxSpeed"));
    remote.controller(15).send(remote.target("/WormScene/flockParticles/cohesionThreshold"));
    remote.controller(16).send(remote.target("/WormScene/followPaths/maxForce"));
    remote.controller(17).send(remote.target("/WormScene/followPaths/maxSpeed"));
    remote.controller(18).send(remote.target("/WormScene/followPaths/radius"));
    remote.controller(19).send(remote.target("/WormScene/particleWebRenderer/lineThreshold"));

    remote.controller(13).send(remote.target("/ShiftingEdgeScene/quadMod"));
    remote.controller(14).send(remote.target("/ShiftingEdgeScene/triangleMod"));
    remote.controller(15).send(remote.target("/ShiftingEdgeScene/lineMod"));

    remote.controller(13).smooth(0.5f).send(remote.target("/GradientTriangleScene/color1Rotation"));
    remote.controller(14).smooth(0.2f).send(remote.target("/GradientTriangleScene/color2Rotation"));



    remote.controller(13).send(remote.target("/ContourScene/contourComponent/resolution"));
    remote.controller(14).send(remote.target("/ContourScene/contourComponent/noiseAmplitude"));
    remote.controller(15).send(remote.target("/ContourScene/contourComponent/noiseScale"));

//    remote.controller(13).smooth(0.2f).send(remote.target("/TriangleScene/xMod"));
//    remote.controller(14).smooth(0.2f).send(remote.target("/TriangleScene/yMod"));
//    remote.controller(15).smooth(0.1f).send(remote.target("/TriangleScene/triangleMod"));
//    remote.controller(16).smooth(0.05f).send(remote.target("/TriangleScene/shiftAmount"));
    remote.controller(13).send(remote.target("/TriangleScene/xMod"));
    remote.controller(14).send(remote.target("/TriangleScene/yMod"));
    remote.controller(15).send(remote.target("/TriangleScene/triangleMod"));
    remote.controller(16).smooth(0.1f).send(remote.target("/TriangleScene/shiftAmount"));

    remote.controller(17).send(remote.target("/TriangleScene/speed"));

    remote.controller(13).send(remote.target("/PyramidScene/speed"));
    remote.controller(14).send(remote.target("/PyramidScene/amplitude"));
    // remote.controller(15).send(remote.target("/PyramidScene/rotZ"));


    remote.controller(13).send(remote.target("/PsychScene/lineThickness"));
    remote.controller(14).send(remote.target("/PsychScene/numSquares"));
    remote.controller(15).send(remote.target("/PsychScene/squareSpeed"));
  }

  @Override
  public void draw() {
    remote.update();

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
//
//    for (Node n : graphTransformer.getPostTransformGrid().getNodes()) {
//      PVector original = graphTransformer.getPreNode(n).position;
//      n.position.x = (float) (original.x + (noise(original.x, 0, t) - 0.5) * 100);
//      n.position.y = (float) (original.y + (noise(original.y, 1, t) - 0.5) * 100);
//    }
    //t += 0.1f;
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
      gridScene.preSetup(graphTransformer.getPreTransformGrid());
    } else {
      gridScene.preSetup(graphTransformer.getPostTransformGrid());
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
