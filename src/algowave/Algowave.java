package algowave;

import algowave.leap.LeapController;
import algowave.leap.LeapMessage;
import algowave.scenes.FlowScene;
import algowave.scenes.WormScene;
import codeanticode.syphon.SyphonServer;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import controlP5.ControlP5;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PJOGL;
import scene.SceneMixer;
import spacefiller.remote.Mod;

public class Algowave extends PApplet {
  private static int CONTROL_PANEL_WIDTH = 500;
  private static int CONTROL_PANEL_HEIGHT = 500;

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

  public void settings() {
    size(CONTROL_PANEL_WIDTH, CONTROL_PANEL_HEIGHT, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    PGraphics mainCanvas = createGraphics(1920, 1080, P3D);
    mixer = new SceneMixer();
    mixer.setOutput(mainCanvas);

    server = new SyphonServer(this, this.getClass().getName());

    flowScene.setAlwaysReset(false);
    wormScene.setAlwaysReset(false);

    mixer.addScene(flowScene);
    mixer.addScene(wormScene);

    leapController = new LeapController();
    leapController.register(this);

    leapController
        .controller(LeapMessage.X_AXIS)
        .send(leapController.target("/Algowave/wormScene/flockParticles/desiredSeparation"));
    leapController.printAddresses();

    controlP5 = new ControlP5(this);
    controlP5.addFrameRate().setInterval(10).setPosition(10, 10);
    float y = 320;
    for (LeapMessage message : leapController.getPatchedMessages()) {
      controlP5
          .addSlider(message.toString())
          .setPosition(10, y)
          .setWidth(CONTROL_PANEL_WIDTH - 100)
          .setMin(0)
          .setMax(1);

      y += 10;
    }
  }

  public void draw() {
    // Draw main output
    mixer.beginDraw();
    mixer.draw();
    mixer.endDraw();

    server.sendImage(mixer.getFrame());

    // Draw control panel
    background(0);

    float previewWidth = CONTROL_PANEL_WIDTH - 20;
    float previewHeight = (float) CONTROL_PANEL_WIDTH / mixer.getOutputWidth() * mixer.getOutputHeight();
    image(mixer.getFrame(), 10, 30, previewWidth, previewHeight);

    stroke(255);
    noFill();
    rect(10, 30, previewWidth, previewHeight);

    for (LeapMessage message : leapController.getPatchedMessages()) {
      controlP5.get(message.toString()).setValue(
          (Float) leapController.controller(message).getLastValue());
    }
  }

  public void keyPressed() {
    if (key == ' ') {
      mixer.gotoNextScene();
    }
  }
}
