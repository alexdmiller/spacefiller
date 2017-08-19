package algoplex2;

import processing.core.PApplet;
import processing.opengl.PJOGL;

public class Algoplex2 extends PApplet {
  public static Algoplex2 instance;

  public static void main(String[] args) {
    main("algoplex2.Algoplex2");
  }

  public Algoplex2() {
    Algoplex2.instance = this;
  }

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {

  }

  public final void draw() {

  }
}
