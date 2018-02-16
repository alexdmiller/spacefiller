package algowave;

import algowave.leap.LeapController;
import algowave.leap.LeapMessage;
import algowave.leap.LeapVisualizer;
import algowave.scenes.FlowScene;
import algowave.scenes.WormScene;
import codeanticode.syphon.SyphonServer;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import controlP5.ControlP5;
import controlP5.Group;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PJOGL;
import scene.SceneMixer;
import spacefiller.remote.Mod;

public class Algowave extends PApplet {
  private static int PADDING = 10;
  private static int CONTROL_PANEL_WIDTH = 1000 + PADDING * 4;
  private static int CONTROL_PANEL_HEIGHT = 500;
  private static int COLUMN_WIDTH = 500;

  public static void main(String[] args) {
    main("algowave.Algowave");
  }

  @Mod
  public FlowScene flowScene = new FlowScene();

  @Mod
  public WormScene wormScene = new WormScene();

  private SceneMixer mixer;
  private SyphonServer server;
  private ControlP5 controlP5;
  private LeapController leapController;
  private LeapVisualizer leapVisualizer;

  public void settings() {
    size(CONTROL_PANEL_WIDTH, CONTROL_PANEL_HEIGHT, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    leapController = new LeapController();
    leapController.register(this);

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


    leapController
        .controller(LeapMessage.Y_AXIS)
        .scale(10, 200)
        .send(leapController.target("/Algowave/wormScene/flockParticles/desiredSeparation"));

    leapController.printAddresses();

    controlP5 = new ControlP5(this);

    controlP5.addGroup("leapviz")
        .setLabel("Leap Visualization")
        .setPosition(PADDING, 10)
        .setWidth(COLUMN_WIDTH)
        .disableCollapse()
        .addCanvas(new PreviewCanvas(leapVisualizer.getFrame(), COLUMN_WIDTH));

    controlP5.addGroup("output")
        .setLabel("Output")
        .setPosition(COLUMN_WIDTH + PADDING * 2, 10)
        .setWidth(COLUMN_WIDTH)
        .disableCollapse()
        .addCanvas(new PreviewCanvas(mixer.getFrame(), COLUMN_WIDTH));

    controlP5.addGroup("scene")
        .setLabel("Scene")
        .disableCollapse()
        .setPosition(COLUMN_WIDTH + PADDING * 2, 300);


//    controlP5.addFrameRate().setInterval(10).setPosition(10, 10);
//
//    Group leapControls = controlP5.addGroup("leapControls")
//        .setPosition(PADDING, 300)
//        .hideBar();
//
//    float y = 0;
//    for (LeapMessage message : leapController.getPatchedMessages()) {
//      controlP5
//          .addSlider(message.toString())
//          .setPosition(0, y)
//          .setWidth(COLUMN_WIDTH)
//          .setMin(0)
//          .setMax(1)
//          .setGroup(leapControls);
//
//      y += 10;
//    }
  }

  public void draw() {
    mixer.draw();
    leapVisualizer.draw();

    // Draw main output
    server.sendImage(mixer.getFrame());

    // Draw control panel
    background(0);

//    for (LeapMessage message : leapController.getPatchedMessages()) {
//      controlP5.get(message.toString()).setValue(
//          (Float) leapController.controller(message).getLastValue());
//    }

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
