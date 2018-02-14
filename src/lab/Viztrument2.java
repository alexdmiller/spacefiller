package lab;

import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PVector;
import processing.opengl.PJOGL;

import java.util.*;

/**
 * Created by miller on 12/18/17.
 */
public class Viztrument2 extends PApplet {
  public static void main(String[] args) {
    main("lab.Viztrument2");
  }

  OscP5 oscP5;

  List<Integer> notes = new ArrayList<>();
  List<PVector> holes = new ArrayList<PVector>();


  float NOISE = 500;
  float X_NOISE_SCALE = 500;
  float Y_NOISE_SCALE = 500;
  float THRESH = 20;
  float MOUSE_THRESH = 500;

  public void settings() {
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    oscP5 = new OscP5(this, 2346);
  }

  private int lastVelocity = 0;
  float t = 0;

  public void oscEvent(OscMessage oscMessage) {
    synchronized (notes) {
      if (oscMessage.addrPattern().startsWith("/Velocity")) {
        lastVelocity = oscMessage.get(0).intValue();
      } else {
        int note = oscMessage.get(0).intValue();
        if (lastVelocity == 0) {
          // off
          notes.remove(notes.indexOf(note));
        } else {
          // on
          notes.add(note);
        }
      }
    }
  }

  public void draw() {
    background(0);

    synchronized (notes) {
      background(0);
      noFill();
      stroke(255);
      strokeWeight(2);
      List<PVector> points = new ArrayList<PVector>();

      for (float y = 0; y < height; y += 30) {
        beginShape();
        List<PVector> line = new ArrayList<PVector>();
        for (float x = 0; x < width + 20; x += 30) {
          PVector p = new PVector(
              x + noise(x / X_NOISE_SCALE, y / Y_NOISE_SCALE, 100 + t) * NOISE - NOISE / 2,
              y + noise(x / X_NOISE_SCALE, y / Y_NOISE_SCALE, t) * NOISE - NOISE / 2);

          for (int n : notes) {
            float breakX = (n % 12) / 12f * width;
            if (Math.abs(breakX - x) < 100) {
              p.y += Math.abs(breakX - x);
            }
          }


//          for (int n : notes) {
//            PVector hole = new PVector((n % 12) / 12f * width, height / 2);
//            PVector delta = PVector.sub(p, hole);
//            if (delta.mag() < MOUSE_THRESH) {
//              delta.setMag(10000 / delta.mag());
//              delta.limit(200);
//              p.add(delta);
//            }
//          }

          //for (int i = 0; i < points.size(); i++) {
          //  delta = PVector.sub(p, points.get(i));

          //  if (delta.mag() < THRESH) {
          //    delta.setMag(1 / delta.mag() * 50);
          //    p.add(delta);
          //   // line(p.x, p.y, points.get(i).x, points.get(i).y);
          //  }
          //}

          vertex(p.x, p.y);
          line.add(p);
        }
        points.addAll(line);
        endShape();
      }
    }

    t += 0.001f;
  }
}
