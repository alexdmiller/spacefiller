package algoplex2;

import algoplex2.scenes.*;
import spacefiller.graph.GridUtils;
import spacefiller.graph.Node;
import spacefiller.graph.renderer.BasicGraphRenderer;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PJOGL;
import processing.serial.Serial;
import scene.Scene;
import scene.SceneApplet;
import spacefiller.mapping.GraphTransformer;
import spacefiller.mapping.Mapper;
import spacefiller.mapping.Quad;
import spacefiller.mapping.Surface;
import spacefiller.remote.MidiRemoteControl;
import spacefiller.remote.Mod;
import spacefiller.remote.SerialRemoteControl;
import spacefiller.remote.SerialStringRemoteControl;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 4;
  private static int COLS = 6;
  private static int SPACING = 200;

  private static String controllerSerialPort;

  public static void main(String[] args) {
    if (args.length == 1) {
      controllerSerialPort = args[0];
      println("controller port = " + controllerSerialPort);
      main("algoplex2.Algoplex2");
    } else {
      println("usage:");
      println("java -Djava.library.path=\"linux64\" -jar algoplex2.jar [controller port]");
      println();
      println("available ports:");

      for (String port : Serial.list()) {
        println(port);
      }
    }
  }

  // private AlgoplexController remote;
  private Mapper mapper;
  private Surface surface;
  private BasicGraphRenderer graphRenderer;
  private boolean showUI = false;
  private TransitionAnimation transition;
  private NumberTransition numberTransition;

  private transient Quad crop;
  private transient PVector selectedCropNode;
  private transient boolean showMouse;

  private SerialStringRemoteControl remote;
  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    fullScreen(P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    mapper = Mapper.load("algoplex2", this);
    mapper.manageCursor();

    surface = mapper.createSurface("main_surface", ROWS, COLS, SPACING);

    remote = new SerialStringRemoteControl(controllerSerialPort, 9600, 7);


    FollowEdges followEdges = new FollowEdges();
    addGridScene(followEdges);
    remote.controller(0).scale(0, 0.02f).toField(followEdges.wobbler, "updateSpeed");
    remote.controller(1).scale(0, 100).toField(followEdges.wobbler, "jitter");
    remote.controller(2).scale(0, 10).toField(followEdges.wobbler, "waveAmp");
    remote.controller(3).scale(0, 1f).toField(followEdges.sinGraphRenderer, "speed");
    remote.controller(4).scale(0, 10).toField(followEdges.sinGraphRenderer, "size");
    remote.controller(5).scale(1, 10).toField(followEdges.sinGraphRenderer, "freq");

    FlowScene flowScene = new FlowScene();
    addGridScene(flowScene);
    remote.controller(0).scale(0.5f, 15).smooth(0.1f).toField(flowScene.perlinFlow, "flowForce");
    remote.controller(1).scale(100, 1000).smooth(0.5f).toField(flowScene.perlinFlow, "noiseScale");
    remote.controller(2).scale(0, 1).toField(flowScene.perlinFlow, "lineSparsity");
    remote.controller(3).scale(0f, 0.5f).toField(flowScene.perlinFlow, "scrollSpeed");
    remote.controller(4).scale(0f, 0.05f).toField(flowScene.perlinFlow, "noiseSpeed1");
    remote.controller(5).scale(0, 0.02f).toField(flowScene.perlinFlow, "noiseSpeed2");

    WormScene wormScene = new WormScene();
    addGridScene(wormScene);
    remote.controller(0).scale(0, 2).toField(wormScene.flockParticles, "maxSpeed");
    remote.controller(1).scale(10, 50).toField(wormScene.flockParticles, "desiredSeparation");
    remote.controller(2).scale(0, 300).toField(wormScene.flockParticles, "cohesionThreshold");
    remote.controller(3).scale(0, 300).toField(wormScene.flockParticles, "alignmentThreshold");
    remote.controller(4).scale(0, 200).toField(wormScene.repelFixedPoints, "repelThreshold");
    remote.controller(5).scale(-.01f, 0.01f).toField(wormScene.repelFixedPoints, "repelStrength");

    TinyTriangleScene tinyTriangleScene = new TinyTriangleScene();
    addGridScene(tinyTriangleScene);
    remote.controller(0).smooth(0.2f).scale(0, 1).toField(tinyTriangleScene, "interpolation");
    remote.controller(1).smooth(0.2f).scale(-.1f, .1f).toField(tinyTriangleScene, "speed"); //.send(remote.target("/TinyTriangleScene/speed"));
    remote.controller(2).smooth(0.2f).scale(0, 0.1f).toField(tinyTriangleScene, "scrollSpeed");
    remote.controller(2).smooth(0.5f).scale(0, 1).toField(tinyTriangleScene, "waveShift");
    remote.controller(3).smooth(0.2f).scale(0.01f, 0.5f).toField(tinyTriangleScene, "circleScale");
    remote.controller(3).smooth(0.5f).scale(0.005f, 0.05f).toField(tinyTriangleScene, "scale");
    remote.controller(4).smooth(0.2f).scale(0, 0.6283185307179586f).toField(tinyTriangleScene, "color1Rotation");
    remote.controller(5).smooth(0.2f).scale(0, -0.39269908169872414f).toField(tinyTriangleScene, "color2Rotation");



    TriangleScene triangleScene = new TriangleScene();
    addGridScene(triangleScene);
    remote.controller(0).scale(0, 1).smooth(0.2f).toField(triangleScene, "mix");
    remote.controller(1).scale(0, 5).smooth(0.2f).toField(triangleScene, "yMod");
    remote.controller(2).scale(0, 5).smooth(0.2f).toField(triangleScene, "xMod");
    remote.controller(3).scale(0, 10).smooth(0.2f).toField(triangleScene, "triangleMod");
    remote.controller(4).scale(0, 10).smooth(0.2f).toField(triangleScene, "lineMod");
    remote.controller(5).scale(0, 20).smooth(0.1f).toField(triangleScene, "shiftAmount");



    remote.connect();


    transition = new TransitionAnimation();
    transition.reset();

    super.setup();
  }

//  private void setupScenes() {
//    GridScene[] gridScenes = new GridScene[] {
//        new WormScene(),
//        new TinyTriangleScene(),
//        new FollowEdges(),
//        new TriangleScene(),
//        new FlowScene(),
//    };
//
//
//    transition = new TransitionAnimation();
//    transition.reset();
//    super.setup();
//  }

  private void setupRemote(String portName) {
    try {
//      remote = new AlgoplexController(portName, 9000);

      //remote = new MidiRemoteControl("Launch Control XL 8", 13);

//      for (Scene scene : scenes) {
//        remote.register(scene);
//      }
//
//      remote.register(this);
//      remote.printAddresses();
//
//      remote.controller(6).send(remote.target("/Algoplex2/gotoNextScene"));

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

//      remote.controller(1).send(remote.target("/CircleScene/bigFreq"));
//      remote.controller(0).send(remote.target("/CircleScene/bigAmp"));
//      remote.controller(2).send(remote.target("/CircleScene/smallAmp"));
//      remote.controller(3).send(remote.target("/CircleScene/smallFreq"));
//      remote.controller(4).send(remote.target("/CircleScene/spread"));
//      remote.controller(5).send(remote.target("/CircleScene/noiseAmp"));
//
//      remote.controller(0).send(remote.target("/LifeScene/addNoise"));
//      remote.controller(1).send(remote.target("/LifeScene/birthChance"));
//      remote.controller(2).send(remote.target("/LifeScene/deathChance"));
//

//

//
//
//      remote.controller(0).send(remote.target("/PerlinTriangles/threshold"));
//      remote.controller(1).send(remote.target("/PerlinTriangles/color1Rotation"));
//      remote.controller(2).send(remote.target("/PerlinTriangles/color2Rotation"));
//      remote.controller(3).send(remote.target("/PerlinTriangles/scale1"));
//      remote.controller(4).send(remote.target("/PerlinTriangles/scale2"));
//      remote.controller(5).smooth(0.1f).send(remote.target("/PerlinTriangles/offset"));
//
//      remote.controller(0).smooth(0.15f).send(remote.target("/CrossScene/crossSize"));
//      remote.controller(1).smooth(0.15f).send(remote.target("/CrossScene/xSize"));
//      remote.controller(2).smooth(0.15f).send(remote.target("/CrossScene/rotation"));
//      remote.controller(3).smooth(0.15f).send(remote.target("/CrossScene/crossRotation"));
//      remote.controller(4).smooth(0.05f).send(remote.target("/CrossScene/color1Rotation"));
//      remote.controller(5).smooth(0.05f).send(remote.target("/CrossScene/color2Rotation"));
//
//      remote.controller(0).smooth(0.05f).send(remote.target("/ColorBath/color1Rotation"));
//      remote.controller(1).smooth(0.05f).send(remote.target("/ColorBath/color2Rotation"));
//      remote.controller(2).smooth(0.05f).send(remote.target("/ColorBath/color3Rotation"));
//      remote.controller(3).smooth(0.05f).send(remote.target("/ColorBath/color4Rotation"));
//
//      remote.controller(0).send(remote.target("/VeinScene/treeComponent/tree/foodJitter"));
//      remote.controller(0).send(remote.target("/VeinScene/treeComponent/foodBrightness"));
//      remote.controller(1).send(remote.target("/VeinScene/treeComponent/tree/forceJitter"));
//      remote.controller(2).send(remote.target("/VeinScene/treeComponent/pulsePeriod"));
//      remote.controller(3).send(remote.target("/VeinScene/treeComponent/growthSpeed"));
//      remote.controller(4).send(remote.target("/VeinScene/treeComponent/edgeThickness"));
//      remote.controller(5).send(remote.target("/VeinScene/treeComponent/attractorInfluenceRadius"));
//
//      remote.controller(0).send(remote.target("/WormScene/flockParticles/maxSpeed"));
//      remote.controller(1).send(remote.target("/WormScene/flockParticles/desiredSeparation"));
//      remote.controller(2).send(remote.target("/WormScene/flockParticles/cohesionThreshold"));
//      remote.controller(3).send(remote.target("/WormScene/flockParticles/alignmentThreshold"));
//      remote.controller(4).send(remote.target("/WormScene/repelFixedPoints/repelThreshold"));
//      remote.controller(5).send(remote.target("/WormScene/repelFixedPoints/repelStrength"));
//
////    remote.controller(2).send(remote.target("/FlowScene/flockParticles/cohesionThreshold"));
//
////    remote.controller(6).send(remote.target("/FlowScene/particleWebRenderer/lineThreshold"));
//
//      remote.controller(0).send(remote.target("/ShiftingEdgeScene/quadMod"));
//      remote.controller(1).send(remote.target("/ShiftingEdgeScene/triangleMod"));
//      remote.controller(2).send(remote.target("/ShiftingEdgeScene/lineMod"));
//
//      remote.controller(0).smooth(0.5f).send(remote.target("/GradientTriangleScene/color1Rotation"));
//      remote.controller(1).smooth(0.2f).send(remote.target("/GradientTriangleScene/color2Rotation"));
//      remote.controller(2).send(remote.target("/GradientTriangleScene/jitter"));
//
//      remote.controller(0).smooth(0.2f).send(remote.target("/ContourScene/contourComponent/noiseAmplitude"));
//      remote.controller(1).smooth(0.2f).send(remote.target("/ContourScene/contourComponent/sinHeight"));
//      remote.controller(2).send(remote.target("/ContourScene/setResolution"));
//      remote.controller(3).send(remote.target("/ContourScene/contourComponent/xSpeed"));
//      remote.controller(3).send(remote.target("/ContourScene/contourComponent/updateSpeed"));
//      remote.controller(4).smooth(0.5f).send(remote.target("/ContourScene/setRotation"));
//      remote.controller(5).send(remote.target("/ContourScene/setColor"));
//
//      remote.controller(0).smooth(0.2f).send(remote.target("/TriangleScene/mix"));
//      remote.controller(1).smooth(0.2f).send(remote.target("/TriangleScene/yMod"));
//      remote.controller(2).smooth(0.2f).send(remote.target("/TriangleScene/xMod"));
//      remote.controller(3).smooth(0.2f).send(remote.target("/TriangleScene/triangleMod"));
//      remote.controller(4).smooth(0.2f).send(remote.target("/TriangleScene/lineMod"));
//      remote.controller(5).smooth(0.1f).send(remote.target("/TriangleScene/shiftAmount"));
//
//      remote.controller(0).send(remote.target("/PyramidScene/speed"));
//      remote.controller(1).send(remote.target("/PyramidScene/amplitude"));
//      // remote.controller(2).send(remote.target("/PyramidScene/rotZ"));
//
//
//      remote.controller(0).send(remote.target("/PsychScene/lineThickness"));
//      remote.controller(1).send(remote.target("/PsychScene/numSquares"));
//      remote.controller(2).send(remote.target("/PsychScene/squareSpeed"));
    } catch (java.lang.RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void draw() {
    background(0);

    surface.drawToSurface(graphics -> {
      graphics.background(0);

      if (currentScene != null) {
        GridScene gridScene = (GridScene) currentScene;
        currentScene.draw(graphics);
      }

      transition.draw(graphics, surface.getPreTransformGrid());

      if (numberTransition != null) {
        numberTransition.draw(graphics, surface.getPreTransformGrid());

        if (numberTransition.isFinished()) {
          switchScene(numberTransition.getNum());
          numberTransition = null;
        }
      }
    });
  }

  @Override
  public void keyPressed() {
    if (showUI) {

      // TODO: switch to Mapper
      //graphTransformer.keyDown(keyCode);
    } else if (selectedCropNode != null) {
      if (keyCode == LEFT) {
        selectedCropNode.x--;
        System.out.println(selectedCropNode);
      } else if (keyCode == RIGHT) {
        selectedCropNode.x++;
      } else if (keyCode == DOWN) {
        selectedCropNode.y++;
      } else if (keyCode == UP) {
        selectedCropNode.y--;
      }
    }

    if (key == '>') {
      gotoNextScene();
    }
  }

  @Mod
  public void gotoNextScene() {
    currentScene = null;
    numberTransition = new NumberTransition((currentSceneIndex + 1) % scenes.size(), surface.getPreTransformGrid());
    transition.reset();
  }

  public void addGridScene(GridScene gridScene) {
    gridScene.preSetup(surface.getPreTransformGrid());
    addScene(gridScene);
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


    }
  }
}
