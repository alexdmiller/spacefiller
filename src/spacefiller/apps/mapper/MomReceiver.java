package spacefiller.apps.mapper;

import processing.core.PApplet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MomReceiver extends PApplet {
  public static void main(String[] args) {
    PApplet.main("spacefiller.apps.mapper.MomReceiver");
  }

  private long signalTime;

  public void settings() {
    fullScreen(P2D, 1);
  }

  public void setup() {
    background(0);
    frameRate(60);

    Receiver thread = new Receiver();
    thread.start();
  }

  public void draw() {
    background(0);

    if (signalTime > 0) {
      long millis = Instant.now().toEpochMilli();
      if (millis > signalTime) {
        background(255);
        signalTime = 0;
      }
    }
//    if (gotSignal) {
//      gotSignal = false;
//      background(255);
//    }

//    long epochSecond =
    ZonedDateTime zonedDateTime = Instant.now().atZone(ZoneId.systemDefault());
    fill(255);
    textSize(100);
    text(zonedDateTime.getSecond(), width / 2, height / 2);
  }

  class Receiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run() {
      System.out.println("Starting receiver");
      try {
        socket = new MulticastSocket(4446);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);
      } catch (IOException e) {
        e.printStackTrace();
      }

      while (true) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        System.out.println("Receiving...");
        try {
          socket.receive(packet);
        } catch (IOException e) {
          e.printStackTrace();
        }

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] data = packet.getData();
        buffer.put(data, 0, Long.BYTES);
        buffer.flip();
//        Instant instant = Instant.ofEpochMilli();
//        System.out.println(instant.toEpochMilli());
        signalTime = buffer.getLong();
      }
    }
  }
}
