package spacefiller;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;
import spacefiller.math.Vector;

import static processing.core.PConstants.P2D;
import static processing.core.PConstants.P3D;

public class Utils {
  private static PApplet pApplet;

  public static void init(PApplet pApplet) {
    Utils.pApplet = pApplet;
  }

  public static PGraphics createGraphics(float width, float height) {
    return pApplet.createGraphics((int) width, (int) height, P2D);
  }

  public static PGraphics createGraphics(float width, float height, String renderer) {
    return pApplet.createGraphics((int) width, (int) height, renderer);
  }

  public static PGraphics createGraphics(float width, float height, String renderer, String filename) {
    return pApplet.createGraphics((int) width, (int) height, renderer, filename);
  }

  public static PShader createShader(String filename) {
    return pApplet.loadShader(filename);
  }

  public static float noise(float x) {
    return pApplet.noise(x);
  }

  public static float noise(float x, float y) {
    return pApplet.noise(x, y);
  }

  public static float noise(float x, float y, float z) {
    return pApplet.noise(x, y, z);
  }

  public static Vector toVector(PVector p) {
    return new Vector(p.x, p.y, p.z);
  }

  public static PVector toVector(Vector v) {
    return new PVector(v.x, v.y, v.z);
  }

  public static float getMillis() {
    return pApplet.millis();
  }

  public static int lerpColor(int from, int to, float amount) {
    return pApplet.lerpColor(from, to, amount);
  }

  public static PShader loadShader(String s) {
    return pApplet.loadShader(s);
  }

}
