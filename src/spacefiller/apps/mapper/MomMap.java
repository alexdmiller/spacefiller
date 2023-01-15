package spacefiller.apps.mapper;

import processing.core.PApplet;
import processing.event.MouseEvent;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.behaviors.ParticleFriction;
import spacefiller.particles.behaviors.RepelParticles;
import spacefiller.particles.behaviors.SoftBounds;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MomMap extends PApplet {
  public static void main(String[] args) {
    PApplet.main("spacefiller.apps.mapper.MomMap");
  }



  List<Pyramid> pyramids;
  Vector selectedVector;
  Pyramid selectedPyramid;
  Pyramid lastSelectedPyramid;

  public void settings() {
    fullScreen(P2D, 2);
  }

  public void setup() {
    frameRate(60);

    try {
      FileInputStream fileIn = new FileInputStream("map.ser");
      ObjectInputStream in = new ObjectInputStream(fileIn);
      pyramids = (List<Pyramid>) in.readObject();
      in.close();
      fileIn.close();
    } catch (IOException i) {
      i.printStackTrace();
    } catch (ClassNotFoundException c) {
      System.out.println("Employee class not found");
      c.printStackTrace();
    }

    if (pyramids == null) {
      pyramids = new ArrayList<>();
    }
  }

  public void draw() {
    background(0);

    if (selectedVector != null) {
      selectedVector.set(mouseX, mouseY);
    } else if (selectedPyramid != null) {
      selectedPyramid.set(mouseX, mouseY);
    }

    int index = 0;
    for (Pyramid pyramid : pyramids) {
      pyramid.draw(this.getGraphics(), frameCount, index);
      index++;
    }
  }

  public void keyPressed() {
    if (key == 'p') {
      pyramids.add(new Pyramid(new Vector(mouseX, mouseY), 50));
    } else if (key == 'd') {
      pyramids.remove(lastSelectedPyramid);
    }
  }

  public void mousePressed(MouseEvent event) {
    Vector mouse = new Vector(mouseX, mouseY);
    for (Pyramid pyramid : pyramids) {
      Vector v = pyramid.selectVector(mouse);
      if (v != null) {
        if (event.isShiftDown()) {
          selectedPyramid = pyramid;
          lastSelectedPyramid = pyramid;
        } else {
          selectedVector = v;
        }
      }
    }
  }

  public void mouseReleased() {
    selectedVector = null;
    selectedPyramid = null;

    try {
      FileOutputStream fileOut =
          new FileOutputStream("map.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(pyramids);
      out.close();
      fileOut.close();
    } catch (IOException i) {
      i.printStackTrace();
    }
  }
}
