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
  private static int ROWS = 5;
  private static int COLS = 8;
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

    CrossScene crossScene = new CrossScene();
    addGridScene(crossScene);
    remote.controller(0).smooth(0.15f).toField(crossScene, "crossSize");
    remote.controller(1).smooth(0.15f).toField(crossScene, "xSize");
    remote.controller(2).smooth(0.15f).toField(crossScene, "rotation");
    remote.controller(3).smooth(0.15f).toField(crossScene, "crossRotation");
    remote.controller(4).smooth(0.05f).toField(crossScene, "color1Rotation");
    remote.controller(5).smooth(0.05f).toField(crossScene, "color2Rotation");


    FollowEdges followEdges = new FollowEdges();
    addGridScene(followEdges);
    remote.controller(0).scale(0, 0.02f).toField(followEdges.wobbler, "updateSpeed");
    remote.controller(1).scale(0, 100).toField(followEdges.wobbler, "jitter");
    remote.controller(2).scale(0, 50).toField(followEdges.wobbler, "waveAmp");
    remote.controller(3).smooth(0.5f).scale(0, 1f).toField(followEdges.sinGraphRenderer, "speed");
    remote.controller(4).smooth(0.5f).scale(0, 10).toField(followEdges.sinGraphRenderer, "size");
    remote.controller(5).smooth(0.5f).scale(1, 10).toField(followEdges.sinGraphRenderer, "freq");

    FlowScene flowScene = new FlowScene();
    addGridScene(flowScene);
    remote.controller(0).scale(0.5f, 15).smooth(0.1f).toField(flowScene.perlinFlow, "flowForce");
    remote.controller(1).scale(100, 1000).smooth(0.01f).toField(flowScene.perlinFlow, "noiseScale");
    remote.controller(2).scale(0, 1).toField(flowScene.perlinFlow, "lineSparsity");
    remote.controller(3).scale(0f, 0.5f).toField(flowScene.perlinFlow, "scrollSpeed");
    remote.controller(4).scale(0f, 0.05f).toField(flowScene.perlinFlow, "noiseSpeed1");
    remote.controller(5).scale(0, 0.02f).toField(flowScene.perlinFlow, "noiseSpeed2");

    WormScene wormScene = new WormScene();
    addGridScene(wormScene);
    remote.controller(0).scale(1, 10).toField(wormScene.flockParticles, "maxSpeed");
    remote.controller(1).scale(10, 50).toField(wormScene.flockParticles, "desiredSeparation");
    remote.controller(2).scale(0, 300).toField(wormScene.flockParticles, "cohesionThreshold");
    remote.controller(3).scale(0, 300).toField(wormScene.flockParticles, "alignmentThreshold");
    remote.controller(4).scale(0, 200).toField(wormScene.repelFixedPoints, "repelThreshold");
    remote.controller(5).scale(-.01f, 0.01f).toField(wormScene.repelFixedPoints, "repelStrength");


    TinyTriangleScene tinyTriangleScene = new TinyTriangleScene();
    addGridScene(tinyTriangleScene);
    remote.controller(0).smooth(0.2f).scale(0, 1).toField(tinyTriangleScene, "interpolation");
    remote.controller(1).smooth(0.2f).scale(0.1f, .2f).toField(tinyTriangleScene, "speed"); //.send(remote.target("/TinyTriangleScene/speed"));
    remote.controller(2).smooth(0.1f).scale(0, 0.1f).toField(tinyTriangleScene, "scrollSpeed");
    remote.controller(2).smooth(0.1f).scale(0, 1).toField(tinyTriangleScene, "waveShift");
    remote.controller(3).smooth(0.01f).scale(0.01f, 0.5f).toField(tinyTriangleScene, "circleScale");
    remote.controller(3).smooth(0.5f).scale(0.005f, 0.05f).toField(tinyTriangleScene, "scale");
    remote.controller(4).smooth(0.2f).scale(0, 0.6283185307179586f).toField(tinyTriangleScene, "color1Rotation");
    remote.controller(5).smooth(0.2f).scale(0, -0.39269908169872414f).toField(tinyTriangleScene, "color2Rotation");


    TriangleScene triangleScene = new TriangleScene();
    addGridScene(triangleScene);
    remote.controller(0).scale(0, 1).smooth(0.2f).toField(triangleScene, "mix");
    remote.controller(1).scale(0, 5).smooth(0.2f).toField(triangleScene, "yMod");
    remote.controller(2).scale(0, 5).smooth(0.2f).toField(triangleScene, "xMod");
    remote.controller(3).scale(0, 10).smooth(0.05f).toField(triangleScene, "triangleMod");
    remote.controller(4).scale(0, 10).smooth(0.05f).toField(triangleScene, "lineMod");
    remote.controller(5).scale(0, 20).smooth(0.1f).toField(triangleScene, "shiftAmount");

    remote.controller(6).gate(0.8f).onGateTriggered(() -> {
      gotoNextScene();
    });

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
