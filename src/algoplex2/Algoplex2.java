package algoplex2;

import algoplex2.scenes.*;
import graph.GridUtils;
import graph.renderer.BasicGraphRenderer;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.Scene;
import scene.SceneApplet;
import spacefiller.remote.MidiRemoteControl;

import java.io.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 3;
  private static int COLS = 4;
  private static int SPACING = 300;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private MidiRemoteControl remote;
  private GraphTransformer graphTransformer;
  private BasicGraphRenderer graphRenderer;
  private PGraphics transformedCanvas;
  private boolean showUI = false;
  private TransitionAnimation transition;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings( ) {
    fullScreen(2);
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
        // round 1
        new PerlinGridScene(),
        new LifeScene(),
        new TinyTriangleScene(),

        new CircleScene(),


        // round 2: parameters finished
        new WormScene(),
        new VeinScene(),
        new FollowEdges(),
        new TriangleScene(),
        new FlowScene(),
        new ContourScene(),

        // round 3: parameter order
        new CrossScene(),

        // throw away
//        new GradientTriangleScene(),ø
//        new PsychScene(),
//        new PyramidScene(),
//        new ColorBath(),
//        new ParticleScene()
//        new ShiftingEdgeScene(),

    };

    for (GridScene scene : gridScenes) {
      addGridScene(scene);
    }

    transformedCanvas = createGraphics(COLS * SPACING, ROWS * SPACING, P3D);

    setCanvas(getGraphics());
    setupRemote();

    transition = new TransitionAnimation();

    super.setup();
  }

  private void setupRemote() {
    remote = new MidiRemoteControl("Launch Control XL 8");
    for (Scene scene : scenes) {
      remote.register(scene);
    }

    remote.register(this);

    remote.printAddresses();


    /*
    /CircleScene/bigAmp
/CircleScene/bigFreq
/CircleScene/length
/CircleScene/noiseAmp
/CircleScene/shiftAmount
/CircleScene/smallAmp
/CircleScene/smallFreq
/CircleScene/spread
     */

    remote.controller(13).send(remote.target("/CircleScene/bigAmp"));
    remote.controller(14).send(remote.target("/CircleScene/bigFreq"));
    remote.controller(15).send(remote.target("/CircleScene/smallAmp"));
    remote.controller(16).send(remote.target("/CircleScene/smallFreq"));
    remote.controller(17).send(remote.target("/CircleScene/spread"));
    remote.controller(18).send(remote.target("/CircleScene/noiseAmp"));

    remote.controller(13).send(remote.target("/LifeScene/addNoise"));
    remote.controller(14).send(remote.target("/LifeScene/birthChance"));
    remote.controller(15).send(remote.target("/LifeScene/deathChance"));

    remote.controller(13).send(remote.target("/FollowEdges/wobbler/updateSpeed"));
    remote.controller(14).send(remote.target("/FollowEdges/wobbler/jitter"));
    remote.controller(15).send(remote.target("/FollowEdges/wobbler/waveAmp"));
    remote.controller(16).send(remote.target("/FollowEdges/sinGraphRenderer/speed"));
    remote.controller(17).send(remote.target("/FollowEdges/sinGraphRenderer/size"));
    remote.controller(18).send(remote.target("/FollowEdges/sinGraphRenderer/freq"));
    remote.controller(19).send(remote.target("/FollowEdges/rendererIndex"));

    remote.controller(13).send(remote.target("/TinyTriangleScene/speed"));
    remote.controller(14).send(remote.target("/TinyTriangleScene/circleScale"));
    remote.controller(15).send(remote.target("/TinyTriangleScene/interpolation"));
    remote.controller(16).send(remote.target("/TinyTriangleScene/scale"));
    remote.controller(17).send(remote.target("/TinyTriangleScene/color1Rotation"));
    remote.controller(18).send(remote.target("/TinyTriangleScene/color2Rotation"));

    remote.controller(13).smooth(0.1f).send(remote.target("/FlowScene/perlinFlow/flowForce"));
    remote.controller(14).smooth(0.5f).send(remote.target("/FlowScene/perlinFlow/noiseScale"));
    remote.controller(15).send(remote.target("/FlowScene/perlinFlow/lineSparsity"));
    remote.controller(16).send(remote.target("/FlowScene/perlinFlow/scrollSpeed"));
    remote.controller(17).send(remote.target("/FlowScene/perlinFlow/noiseSpeed1"));
    remote.controller(18).send(remote.target("/FlowScene/perlinFlow/noiseSpeed2"));

    remote.controller(13).send(remote.target("/PerlinTriangles/threshold"));
    remote.controller(14).send(remote.target("/PerlinTriangles/color1Rotation"));
    remote.controller(15).send(remote.target("/PerlinTriangles/color2Rotation"));
    remote.controller(16).send(remote.target("/PerlinTriangles/scale1"));
    remote.controller(17).send(remote.target("/PerlinTriangles/scale2"));
    remote.controller(18).smooth(0.1f).send(remote.target("/PerlinTriangles/offset"));

    remote.controller(13).smooth(0.15f).send(remote.target("/CrossScene/crossSize"));
    remote.controller(14).smooth(0.15f).send(remote.target("/CrossScene/xSize"));
    remote.controller(15).smooth(0.15f).invert().send(remote.target("/CrossScene/rotation"));
    remote.controller(16).smooth(0.15f).invert().send(remote.target("/CrossScene/crossRotation"));
    remote.controller(17).smooth(0.05f).send(remote.target("/CrossScene/color1Rotation"));
    remote.controller(18).smooth(0.05f).send(remote.target("/CrossScene/color2Rotation"));

    remote.controller(13).smooth(0.05f).send(remote.target("/ColorBath/color1Rotation"));
    remote.controller(14).smooth(0.05f).send(remote.target("/ColorBath/color2Rotation"));
    remote.controller(15).smooth(0.05f).send(remote.target("/ColorBath/color3Rotation"));
    remote.controller(16).smooth(0.05f).send(remote.target("/ColorBath/color4Rotation"));

    remote.controller(13).send(remote.target("/VeinScene/treeComponent/tree/foodJitter"));
    remote.controller(14).send(remote.target("/VeinScene/treeComponent/tree/forceJitter"));
    remote.controller(15).send(remote.target("/VeinScene/treeComponent/pulsePeriod"));
    remote.controller(16).send(remote.target("/VeinScene/treeComponent/growthSpeed"));
    remote.controller(17).send(remote.target("/VeinScene/treeComponent/edgeThickness"));

    remote.controller(13).send(remote.target("/WormScene/flockParticles/maxSpeed"));
    remote.controller(14).send(remote.target("/WormScene/flockParticles/desiredSeparation"));
    remote.controller(15).send(remote.target("/WormScene/flockParticles/cohesionThreshold"));
    remote.controller(16).send(remote.target("/WormScene/flockParticles/alignmentThreshold"));
    remote.controller(17).send(remote.target("/WormScene/repelFixedPoints/repelThreshold"));
    remote.controller(18).send(remote.target("/WormScene/repelFixedPoints/repelStrength"));

//    remote.controller(15).send(remote.target("/WormScene/flockParticles/cohesionThreshold"));

//    remote.controller(19).send(remote.target("/WormScene/particleWebRenderer/lineThreshold"));

    remote.controller(13).send(remote.target("/ShiftingEdgeScene/quadMod"));
    remote.controller(14).send(remote.target("/ShiftingEdgeScene/triangleMod"));
    remote.controller(15).send(remote.target("/ShiftingEdgeScene/lineMod"));

    remote.controller(13).smooth(0.5f).send(remote.target("/GradientTriangleScene/color1Rotation"));
    remote.controller(14).smooth(0.2f).send(remote.target("/GradientTriangleScene/color2Rotation"));
    remote.controller(15).send(remote.target("/GradientTriangleScene/jitter"));


    remote.controller(13).send(remote.target("/ContourScene/contourComponent/noiseAmplitude"));
    remote.controller(14).send(remote.target("/ContourScene/contourComponent/sinHeight"));
    remote.controller(15).send(remote.target("/ContourScene/setResolution"));
    remote.controller(16).send(remote.target("/ContourScene/contourComponent/xSpeed"));
    remote.controller(16).send(remote.target("/ContourScene/contourComponent/updateSpeed"));
    remote.controller(17).send(remote.target("/ContourScene/setRotation"));
    remote.controller(18).send(remote.target("/ContourScene/setColor"));

    remote.controller(13).send(remote.target("/TriangleScene/mix"));
    remote.controller(14).send(remote.target("/TriangleScene/yMod"));
    remote.controller(15).send(remote.target("/TriangleScene/xMod"));
    remote.controller(16).send(remote.target("/TriangleScene/triangleMod"));
    remote.controller(17).send(remote.target("/TriangleScene/lineMod"));
    remote.controller(18).smooth(0.1f).send(remote.target("/TriangleScene/shiftAmount"));

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
    transition.draw(this.transformedCanvas, graphTransformer.getPreTransformGrid());
    this.transformedCanvas.endDraw();

    graphTransformer.drawImage(this.canvas, this.transformedCanvas);

    if (showUI) {
      graphTransformer.drawUI(this.canvas);
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

  @Override
  public void switchScene(int sceneIndex) {
    if (currentScene != null) {
      currentScene.teardown();
    }

    if (sceneIndex < scenes.size()) {
      Scene scene = scenes.get(sceneIndex);
      currentSceneIndex = sceneIndex;

      scene.setup();

      currentScene = scene;

      transition.reset();
    }
  }
}
