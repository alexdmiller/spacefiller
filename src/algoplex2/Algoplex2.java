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

import java.io.*;
import java.util.*;

public class Algoplex2 extends SceneApplet {
  public static Algoplex2 instance;
  private static int ROWS = 4;
  private static int COLS = 6;
  private static int SPACING = 200;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  private AlgoplexController remote;
  private Mapper mapper;
  private Surface surface;
  private BasicGraphRenderer graphRenderer;
  private boolean showUI = false;
  private TransitionAnimation transition;
  private NumberTransition numberTransition;

  private static final String MAC_SERIAL_PORT = "/dev/tty.usbmodem1679111";
  private static final String WINDOWS_SERIAL_PORT = "COM4";

  private transient Quad crop;
  private transient PVector selectedCropNode;
  private transient boolean showMouse;

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    fullScreen(21);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    noCursor();
    setupScenes();

    String[] availablePorts = Serial.list();

    if (availablePorts.length == 0) {
      System.out.println("No ports available. Skipping controller setup.");
    } else {
      System.out.println("Available ports:");
      for (int i = 0; i < availablePorts.length; i++) {
        System.out.println("[" + i + "]: " + availablePorts[i]);
      }

      System.out.print("Type port number and press enter: ");
      Scanner scanner = new Scanner(System.in);
      int portNum = scanner.nextInt();

      if (portNum > 0) {
        String portName = availablePorts[portNum];

        System.out.println("Using port " + portName);
        setupRemote(portName);
      }
    }
//
//    List<String> serialPorts = Arrays.asList(Serial.list());
//    if (serialPorts.contains(MAC_SERIAL_PORT)) {
//      setupRemote(MAC_SERIAL_PORT);
//    } else if (serialPorts.contains(WINDOWS_SERIAL_PORT)) {
//      setupRemote(WINDOWS_SERIAL_PORT);
//    }
//
//    background(0);
//    PFont f = createDefaultFont(30);
//    textFont(f);
//    text("ALGOPLEX II", 100, 100);
//    text("Plug in controller to start.", 100, 200);
  }

  private void setupScenes() {
    mapper = new Mapper(this);

    // loadGraphs();

    if (surface == null) {
      surface = mapper.createSurface(ROWS, COLS, SPACING);
    }

    graphRenderer = new BasicGraphRenderer(5);
    graphRenderer.setColor(0xFFFFFF00);

    GridScene[] gridScenes = new GridScene[] {
//        new CircleScene(),
//        new LifeScene(),
//        new GradientTriangleScene(),
//        new PsychScene(),
//        new PyramidScene(),
//        new ColorBath(),
//        new ParticleScene(),
//        new ShiftingEdgeScene(),
        // new PerlinGridScene(),
        new WormScene(),
        new ContourScene(),
        new TinyTriangleScene(),
        new FollowEdges(),
        new TriangleScene(),
        new FlowScene(),
        new CrossScene(),
        new VeinScene(),
        // throw away
    };

    for (GridScene scene : gridScenes) {
      addGridScene(scene);
    }

    setCanvas(getGraphics());
    transition = new TransitionAnimation();
    transition.reset();
    super.setup();
  }

  private void setupRemote(String portName) {
    try {
      remote = new AlgoplexController(portName, 9000);

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
//      remote.controller(0).send(remote.target("/FollowEdges/wobbler/updateSpeed"));
//      remote.controller(1).smooth(0.2f).send(remote.target("/FollowEdges/wobbler/jitter"));
//      remote.controller(2).smooth(0.2f).send(remote.target("/FollowEdges/wobbler/waveAmp"));
//      remote.controller(3).send(remote.target("/FollowEdges/sinGraphRenderer/speed"));
//      remote.controller(4).send(remote.target("/FollowEdges/sinGraphRenderer/size"));
//      remote.controller(5).smooth(0.2f).send(remote.target("/FollowEdges/sinGraphRenderer/freq"));
//      // remote.controller(6).send(remote.target("/FollowEdges/rendererIndex"));
//
//      remote.controller(0).smooth(0.2f).send(remote.target("/TinyTriangleScene/interpolation"));
//      remote.controller(1).smooth(0.2f).send(remote.target("/TinyTriangleScene/speed"));
//      remote.controller(2).smooth(0.2f).send(remote.target("/TinyTriangleScene/scrollSpeed"));
//      remote.controller(2).smooth(0.5f).send(remote.target("/TinyTriangleScene/waveShift"));
//      remote.controller(3).smooth(0.2f).send(remote.target("/TinyTriangleScene/circleScale"));
//      remote.controller(3).smooth(0.5f).send(remote.target("/TinyTriangleScene/scale"));
//      remote.controller(4).smooth(0.2f).send(remote.target("/TinyTriangleScene/color1Rotation"));
//      remote.controller(5).smooth(0.2f).send(remote.target("/TinyTriangleScene/color2Rotation"));
//
//      remote.controller(0).smooth(0.1f).send(remote.target("/FlowScene/perlinFlow/flowForce"));
//      remote.controller(1).smooth(0.5f).send(remote.target("/FlowScene/perlinFlow/noiseScale"));
//      remote.controller(2).send(remote.target("/FlowScene/perlinFlow/lineSparsity"));
//      remote.controller(3).send(remote.target("/FlowScene/perlinFlow/scrollSpeed"));
//      remote.controller(4).send(remote.target("/FlowScene/perlinFlow/noiseSpeed1"));
//      remote.controller(5).send(remote.target("/FlowScene/perlinFlow/noiseSpeed2"));
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
    this.canvas.background(0);

    surface.drawToCanvas(graphics -> {
      graphics.background(0);

      if (currentScene != null) {
        GridScene gridScene = (GridScene) currentScene;
        if (gridScene.isTransformed()) {
          currentScene.draw(graphics);
        }
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
    if (key == ' ') {
      showUI = !showUI;
    }

    if (key == 'n') {
      if (showMouse) {
        showMouse = false;
        noCursor();
      } else {
        showMouse = true;
        cursor();
      }
    }

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

  @Override
  public void mousePressed() {
    if (showUI) {
      // TODO: switch to Mapper
      // graphTransformer.mouseDown(mouseX, mouseY);
    } else {
      PVector mouse = new PVector(mouseX, mouseY);

//      for (PVector quadPoint : crop.getVertices()) {
//        float dist = PVector.dist(quadPoint, mouse);
//        if (dist < 10) {
//          selectedCropNode = quadPoint;
//          return;
//        }
//      }
    }
  }

//  @Override
//  public void mouseReleased() {
//    if (showUI) {
//      // TODO: switch to Mapper
//      // graphTransformer.mouseUp(mouseX, mouseY);
//      saveGraphs();
//      crop = graphTransformer.getPostTransformGrid().getBoundingQuad().copy();
//    }
//  }
//
//  @Override
//  public void mouseDragged() {
//    if (showUI) {
//      // TODO: switch to Mapper
//      // graphTransformer.mouseDragged(mouseX, mouseY);
//    }
//  }

  public void addGridScene(GridScene gridScene) {
    if (gridScene.isTransformed()) {
      gridScene.preSetup(surface.getPreTransformGrid());
    } else {
      gridScene.preSetup(surface.getPostTransformGrid());
    }
    addScene(gridScene);
  }

//  private void saveGraphs() {
//    try {
//      FileOutputStream fileOut =
//          new FileOutputStream("algoplex2.ser");
//      ObjectOutputStream out = new ObjectOutputStream(fileOut);
//      out.writeObject(graphTransformer);
//      out.close();
//      fileOut.close();
//    } catch (IOException i) {
//      i.printStackTrace();
//    }
//  }
//
//  private void loadGraphs() {
//    try {
//      FileInputStream fileIn = new FileInputStream("algoplex2.ser");
//      ObjectInputStream in = new ObjectInputStream(fileIn);
//      graphTransformer = (GraphTransformer) in.readObject();
//      in.close();
//      fileIn.close();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    } catch (IOException i) {
//      i.printStackTrace();
//    } catch (ClassNotFoundException c) {
//      c.printStackTrace();
//    }
//  }

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
