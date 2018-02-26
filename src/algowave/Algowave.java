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
import cz.adamh.utils.NativeUtils;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.SceneMixer;
import spacefiller.remote.Mod;

import java.io.IOException;

public class Algowave extends PApplet {
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
  private ControlP5 controlP5;
  private LeapRemoteControl leapController;
  private LeapVisualizer leapVisualizer;

  public void settings() {
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

    server = new SyphonServer(this, this.getClass().getName());

    flowScene.setAlwaysReset(false);
    wormScene.setAlwaysReset(false);

    mixer.addScene(flowScene);
    mixer.addScene(wormScene);

    leapController.register(this);


    leapController
        .controller(LeapMessage.GRAB)
        .gate(0.9f)
        .send(leapController.target("/Algowave/mixer/gotoNextScene"));

    leapController
        .controller(LeapMessage.Y_AXIS)
        .scale(0, 200)
        .send(leapController.target("/Algowave/wormScene/flockParticles/desiredSeparation"));

    leapController
        .controller(LeapMessage.Y_VELOCITY)
        .smooth(0.2f)
        .scale(2, 100)
        .send(leapController.target("/Algowave/wormScene/flockParticles/maxSpeed"));

    leapController
        .controller(LeapMessage.ROLL)
        .scale(0, 50)
        .send(leapController.target("/Algowave/flowScene/perlinFlow/lineLength"));

    leapController
        .controller(LeapMessage.Y_AXIS)
        .smooth(0.2f)
        .scale(10, 500)
        .send(leapController.target("/Algowave/flowScene/perlinFlow/circleRadius"));

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

    controlP5.addGroup("scene")
        .setHeight(20)
        .setLabel("Scene Parameters")
        .disableCollapse()
        .setWidth(COLUMN_WIDTH)
        .setPosition(COLUMN_WIDTH + PADDING * 2, 390);

    controlP5.addFrameRate().setInterval(10).setPosition(PADDING, height - PADDING * 2);
//
//    Group leapControls = controlP5.addGroup("leapControls")
//        .setPosition(PADDING, 300)
//        .hideBar();
//
//    float y = 0;

  }

  public void draw() {
    leapController.update();

    mixer.draw();
    leapVisualizer.draw();

    // Draw main output
    server.sendImage(mixer.getFrame());

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
