package algowave;

import processing.core.PApplet;
import processing.opengl.PJOGL;

public class Test extends PApplet {
  public static void main(String[] args) {
    main("algowave.Test");
  }

  public void settings() {
    size(500, 500, P3D);
    PJOGL.profile = 1;
  }

  public void setup() {
    background(255, 0, 0);
  }

  public void draw() {

  }
}
