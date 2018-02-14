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
public class Vizstrument extends PApplet {
  public static void main(String[] args) {
    main("lab.Vizstrument");
  }

  OscP5 oscP5;
  List<Particle> particles = new ArrayList<>();
  Map<Integer, Particle> noteToParticle = new HashMap<>();

  public void settings() {
    size(900, 900, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    oscP5 = new OscP5(this, 2346);
  }

  private int lastVelocity = 0;

  public void oscEvent(OscMessage oscMessage) {
    synchronized (particles) {
      if (oscMessage.addrPattern().startsWith("/Velocity")) {
        lastVelocity = oscMessage.get(0).intValue();
      } else {
        int note = oscMessage.get(0).intValue();
        if (lastVelocity == 0) {
          if (noteToParticle.containsKey(note)) {
            Particle p = noteToParticle.remove(note);
            p.active = false;
          }
        } else {
          float theta = ((note % 12) / 12f) * 2 * PI;
          Particle p = new Particle(width / 2 + cos(theta) * 5, height / 2 + sin(theta) * 5);
          p.active = true;
          p.vel.set(cos(theta) * 2, sin(theta) * 2);
          particles.add(p);
          noteToParticle.put(note, p);
        }
      }
    }
  }

  public void draw() {
    background(0);

    synchronized (particles) {
      Iterator<Particle> iter = particles.iterator();
      while (iter.hasNext()) {
        Particle p = iter.next();
        if (p.life <= 0) {
          iter.remove();
        }
      }


      for (int i = 0; i < particles.size(); i++) {
        Particle p = particles.get(i);

        for (int j = 0; j < particles.size(); j++) {
          Particle p2 = particles.get(j);
          PVector delta = PVector.sub(p.pos, p2.pos);
          if (delta.mag() < 700) {
            delta.setMag(10f / (delta.mag() + 0.01f) * p.life * p2.life);
            p.vel.sub(delta);
            p2.vel.add(delta);
          }

          delta = PVector.sub(p.pos, p2.pos);
          if (delta.mag() < 200) {
            delta.setMag(20f / (delta.mag() + 0.01f) * (1 / (p.life + p2.life)));
            p.vel.add(delta);
            p2.vel.sub(delta);
          }

          delta = PVector.sub(p.pos, p2.pos);
          if (delta.mag() < 200 + 50) {
            stroke(p.active && p2.active ? color(255, 0, 0) : color(p.life * 150));
            strokeWeight(p.active && p2.active ? 3 : 1);
            line(p.pos.x, p.pos.y, p2.pos.x, p2.pos.y);
          }
        }
      }
    }

    for (int i = 0; i < particles.size(); i++) {
      Particle p = particles.get(i);

      noFill();
      p.update();

      stroke(p.active ? color(255, 0, 0) : color(p.life * 150));
      strokeWeight(p.active ? 3 : 1);

      ellipse(p.pos.x, p.pos.y, 20, 20);
    }
  }

  static class Particle {
    float life = 1;
    float agingRate = 0.01f;
    float friction = 0.9f;

    PVector pos;
    PVector vel;

    boolean active;

    Particle(float x, float y) {
      pos = new PVector(x, y);
      vel = PVector.random2D();
    }

    void update() {
      pos.add(vel);
      vel.mult(friction);

      if (!active) {
        life -= agingRate;
      }
    }
  }
}
