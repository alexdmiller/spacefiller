package spacefiller.apps.mapper;

import processing.core.PApplet;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.Instant;

public class MomSender extends PApplet {
  public static void main(String[] args) {
    PApplet.main("spacefiller.apps.mapper.MomSender");
  }

  private DatagramSocket socket;
  private InetAddress group;

  public void settings() {
    size(500, 500);
  }

  public void setup() {
    background(0);
    frameRate(60);

    String multicastMessage = "hello!";
    try {
      socket = new DatagramSocket();
      group = InetAddress.getByName("230.0.0.0");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void draw() {


  }

  public void mousePressed() {
    long millis = Instant.now().plusMillis(500).toEpochMilli();
    ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
    byteBuffer.putLong(millis);
    byte[] rawBuffer = byteBuffer.array();
    DatagramPacket packet = new DatagramPacket(rawBuffer, rawBuffer.length, group, 4446);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void keyPressed() {
    if (key == 'q') {
      socket.close();
      exit();
    }
  }
}
