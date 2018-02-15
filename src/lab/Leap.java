package lab;

import codeanticode.syphon.SyphonServer;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Listener;
import processing.core.PApplet;
import processing.opengl.PJOGL;

import java.io.IOException;

/**
 * Created by miller on 2/15/18.
 */
public class Leap extends PApplet {

  public static void main(String[] args) {
    main("lab.Leap");
  }

  Controller controller;
  private SyphonServer server;

  public void settings() {
    size(500, 500, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    server = new SyphonServer(this, this.getClass().getName());

    controller = new Controller();
    SampleListener listener = new SampleListener();
    controller.addListener(listener);

    System.out.println("Press Enter to quit...");
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  static class SampleListener extends Listener {

    public void onConnect(Controller controller) {
      System.out.println("Connected");
    }

    public void onFrame(Controller controller) {
      System.out.println("Frame available");
    }
  }
}
