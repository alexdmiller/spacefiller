package algowave;

import algowave.leap.LeapRemoteControl;
import algowave.leap.LeapMessage;
import algowave.leap.LeapVisualizer;
import algowave.scenes.FlowScene;
import algowave.scenes.WormScene;
import codeanticode.syphon.SyphonServer;
import controlP5.ControlP5;
import controlP5.Group;
import controlP5.Slider;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.SceneMixer;
import spacefiller.remote.Mod;

import java.lang.reflect.Field;
import java.util.Arrays;

import spacefiller.remote.signal.DataReceiver;
import spout.*;

public class Algowave extends PApplet {
  public static boolean windows = false;
  public static PApplet instance;

  static {
    windows = System.getProperty("os.name").startsWith("Windows");

    if (windows) {
      try {
        addLibraryPath(System.getProperty("user.dir") + "\\lib");
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        addLibraryPath(System.getProperty("user.dir") + "/lib");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void addLibraryPath(String pathToAdd) throws Exception{
    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
    usrPathsField.setAccessible(true);

    //get array of paths
    final String[] paths = (String[])usrPathsField.get(null);

    //check if the path to add is already present
    for(String path : paths) {
      if(path.equals(pathToAdd)) {
        return;
      }
    }

    //add the new path
    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
    newPaths[newPaths.length-1] = pathToAdd;
    usrPathsField.set(null, newPaths);
  }

  private static int PADDING = 10;
  private static int CONTROL_PANEL_WIDTH = 1000 + PADDING * 4;
  private static int CONTROL_PANEL_HEIGHT = 800;
  private static int COLUMN_WIDTH = 500;

  public static void main(String[] args) {
    main("algowave.Algowave");

  }

  @Mod
  public FlowScene flowScene = new FlowScene();

  @Mod
  public WormScene wormScene = new WormScene();

  @Mod
  public SceneMixer mixer;

  private SyphonServer server;
  private Spout spout;
  private ControlP5 controlP5;
  private LeapRemoteControl leapController;
  private LeapVisualizer leapVisualizer;

  @Mod
  public Momentum momentum = new Momentum(0.999f);

  public void settings() {
    instance = this;

    size(CONTROL_PANEL_WIDTH, CONTROL_PANEL_HEIGHT, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {

    leapController = new LeapRemoteControl();

    PGraphics mainCanvas = createGraphics(1920, 1080, P3D);
    mixer = new SceneMixer();
    mixer.setOutput(mainCanvas);

    PGraphics vizCanvas = createGraphics(1920 / 2, 1080 / 2, P3D);
    leapVisualizer = new LeapVisualizer(leapController.getController());
    leapVisualizer.setOutput(vizCanvas);

    if (windows) {
      spout = new Spout(this);
      spout.createSender("Algowave", 1920, 1080);
    } else {
      server = new SyphonServer(this, this.getClass().getName());
    }

    flowScene.setAlwaysReset(false);
    wormScene.setAlwaysReset(false);

    mixer.addScene(wormScene);
    mixer.addScene(flowScene);

    leapController.register(this);


//    leapController
//        .controller(LeapMessage.GRAB)
//        .gate(0.9f)
//        .send(leapController.target("/Algowave/mixer/gotoNextScene"));



//    leapController
//        .controller(LeapMessage.Y_VELOCITY)
//        .smooth(0.2f)
//        .scale(2, 100)
//        .send(leapController.target("/Algowave/wormScene/flockParticles/maxSpeed"));

//    leapController
//        .controller(LeapMessage.ROLL)
//        .scale(0, 50)
//        .send(leapController.target("/Algowave/flowScene/perlinFlow/lineLength"));
//
//    leapController
//        .controller(LeapMessage.Y_AXIS)
//        .smooth(0.2f)
//        .scale(10, 500)
//        .send(leapController.target("/Algowave/flowScene/perlinFlow/circleRadius"));

    leapController
        .controller(LeapMessage.SPEED)
        .scale(0, 0.5f)
        .send(momentum);

    //momentum.send(leapController.target("/Algowave/wormScene/waterSpeed"));
    momentum.multiply(0.2f).add(0.1f).send(leapController.target("/Algowave/wormScene/flockParticles/maxForce"));
    momentum.multiply(0.2f).add(0.1f).send(leapController.target("/Algowave/wormScene/flockParticles/maxSpeed"));
    momentum.multiply(0.1f).add(0.1f).send(leapController.target("/Algowave/wormScene/noiseScroll"));
    momentum.multiply(1f).add(0.1f).send(leapController.target("/Algowave/wormScene/flowParticles/maxForce"));
    momentum.multiply(1f).add(0.1f).send(leapController.target("/Algowave/wormScene/flowParticles/weight"));
    momentum.multiply(0.05f).add(0.5f).send(leapController.target("/Algowave/wormScene/waterSpeed"));
    momentum.multiply(0.1f).add(10).send(leapController.target("/Algowave/wormScene/flockParticles/desiredSeparation"));



    momentum.multiply(-0.5f).send(leapController.target("/Algowave/flowScene/perlinFlow/fallSpeed"));
    momentum.multiply(0.5f).add(0.1f).send(leapController.target("/Algowave/flowScene/perlinFlow/flowForce"));
    momentum.multiply(0.0002f).send(leapController.target("/Algowave/flowScene/perlinFlow/noiseSpeed1"));
    momentum.multiply(0.0002f).send(leapController.target("/Algowave/flowScene/perlinFlow/noiseSpeed2"));


//    leapController
//        .controller(LeapMessage.X_VELOCITY)
//        .smooth(0.5f)
//        .multiply(100)
//        .send(leapController.target("/Algowave/wormScene/waterSpeed"));

    leapController
        .controller(LeapMessage.GRAB)
        .smooth(0.9f)
        .invert()
        .scale(0.8f, 0.999f)
        .send(leapController.target("/Algowave/momentum/friction"));

        //.send(leapController.target("/Algowave/wormScene/flockParticles/maxSpeed"));

    leapController.printAddresses();

    controlP5 = new ControlP5(this);

    controlP5.setFont(createFont("Input Mono", 12));
    controlP5.setColorBackground(0);
    controlP5.setColorForeground(color(0));
    controlP5.setColorActive(color(255));

    controlP5.addLabel("title")
        .setText("algowave v0.1")
        .setFont(createFont("Arial", 15, true))
        .setPosition(PADDING, PADDING);

    controlP5.addGroup("leapviz")
        .setLabel("Leap Visualization")
        .setHeight(20)
        .setPosition(PADDING, PADDING * 7)
        .setWidth(COLUMN_WIDTH)
        .disableCollapse()
        .addCanvas(new PreviewCanvas(leapVisualizer.getFrame(), COLUMN_WIDTH));

    Group input = controlP5.addGroup("input")
        .setLabel("Leap Input")
        .setHeight(20)
        .setPosition(PADDING, 390)
        .setWidth(COLUMN_WIDTH)
        .disableCollapse();

    float y = 5;
    for (LeapMessage message : leapController.getPatchedMessages()) {
      controlP5
          .addSlider(message.toString())
          .setPosition(0, y)
          .setHeight(20)
          .setColorForeground(color(100))
          .setWidth(COLUMN_WIDTH - 100)
          .setMin(0)
          .setMax(1)
          .setSliderMode(Slider.FLEXIBLE)
          .setHandleSize(20)
          .setGroup(input);
      y += 20;
    }

    controlP5.addGroup("output")
        .setLabel("Output")
        .setHeight(20)
        .setPosition(COLUMN_WIDTH + PADDING * 2, PADDING * 7)
        .setWidth(COLUMN_WIDTH)
        .disableCollapse()
        .addCanvas(new PreviewCanvas(mixer.getFrame(), COLUMN_WIDTH));

    Group scene = controlP5.addGroup("scene")
        .setHeight(20)
        .setLabel("Scene Parameters")
        .disableCollapse()
        .setWidth(COLUMN_WIDTH)
        .setPosition(COLUMN_WIDTH + PADDING * 2, 390);

    controlP5
        .addSlider("momentum")
        .setPosition(0, 0)
        .setHeight(20)
        .setColorForeground(color(100))
        .setWidth(COLUMN_WIDTH - 100)
        .setMin(0)
        .setMax(20)
        .setSliderMode(Slider.FLEXIBLE)
        .setHandleSize(20)
        .setGroup(scene);

    controlP5.addFrameRate().setInterval(10).setPosition(PADDING, height - PADDING * 2);
//
//    Group leapControls = controlP5.addGroup("leapControls")
//        .setPosition(PADDING, 300)
//        .hideBar();
//
//    float y = 0;
    mixer.gotoNextScene();
  }

  public void draw() {
    if (momentum.getLastValue() != null && (float) momentum.getLastValue() > 20) {
      momentum.kill();
      mixer.gotoNextScene();
    }

    leapController.update();

    mixer.draw();
    leapVisualizer.draw();

    // Draw main output
    if (windows) {
      spout.sendTexture(mixer.getFrame());
    } else {
      server.sendImage(mixer.getFrame());
    }

    // Draw control panel
    background(20);

    stroke(255);
    strokeWeight(1);
    fill(0);
    rect(-2, -2, width + 10, PADDING * 4);

    for (LeapMessage message : leapController.getPatchedMessages()) {
      controlP5.get(message.toString()).setValue(
          (Float) leapController.controller(message).getLastValue());
    }

    controlP5.get("momentum").setValue((Float) momentum.getLastValue());

//
//    pushMatrix();
//
//    // Top
//    translate(PADDING, PADDING);
//
//    // Left column
//    translate(0, 50);
//    PImage vizFrame = leapVisualizer.getFrame();
//    image(vizFrame, 0, 0);
//    stroke(255);
//    strokeWeight(2);
//    noFill();
//    rect(0, 0, vizFrame.width, vizFrame.height);
//

//
//
//    // Right column
//    translate(COLUMN_WIDTH + PADDING * 2, 0);
//    int previewHeight = (int) (COLUMN_WIDTH / (float) mixer.getOutputWidth() * mixer.getOutputHeight());
//    image(mixer.getFrame(), 0, 0, COLUMN_WIDTH, previewHeight);
//
//    stroke(255);
//    noFill();
//    strokeWeight(2);
//    rect(0, 0, COLUMN_WIDTH, previewHeight);
//
//    popMatrix();
  }

  public void keyPressed() {
    if (key == ' ') {
      mixer.gotoNextScene();
    }
  }
}
