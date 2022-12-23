package spacefiller.apps.tests;

import processing.core.PApplet;
import processing.core.PGraphics;

public class SVGTest extends PApplet {
  public static void main(String[] args) {
    PApplet.main("spacefiller.apps.tests.SVGTest");
  }

  public void settings() {
    size(500, 500);
  }

  public void setup() {
    PGraphics svg = createGraphics(300, 300, SVG, "/Users/alex/projects/spacefiller/output.svg");
    svg.beginDraw();
    svg.background(128, 0, 0);
    svg.line(50, 50, 250, 250);
    svg.dispose();
    svg.endDraw();
  }
}
