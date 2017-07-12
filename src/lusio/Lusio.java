package lusio;

import codeanticode.syphon.SyphonServer;
import common.Bounds;
import lusio.generators.MeshGenerator;
import lusio.generators.SceneGenerator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PJOGL;

import java.util.ArrayList;
import java.util.List;

public class Lusio extends PApplet {
  private static PApplet instance;

  public static void main(String[] args) {
    main("lusio.Lusio");
  }

  private SyphonServer server;
  private PGraphics canvas;
  private List<SceneGenerator> generators;

  public Lusio() {
    Lusio.instance = this;
    generators = new ArrayList<>();

    SceneGenerator gen = new MeshGenerator(100, 10);
    gen.setPos(width / 2, height / 2);
    gen.setBounds(new Bounds(100, 300, 400));
    generators.add(gen);
  }

  public void settings() {
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  public final void setup() {
    canvas = createGraphics(1920, 1080, P3D);
    canvas.smooth();
    server = new SyphonServer(this, this.getClass().getName());

    for (SceneGenerator generator : generators) {
      generator.setup();
    }
  }

  private void drawGenerators() {
    canvas.ortho();
    for (SceneGenerator generator : generators) {
      canvas.pushMatrix();
      canvas.translate(generator.getX(), generator.getY());
      generator.draw(canvas);
      canvas.popMatrix();
    }
  }

  public final void draw() {
    canvas.beginDraw();

    canvas.background(0);
    canvas.translate(width / 2, height / 2);
    canvas.noFill();
    canvas.stroke(255);

    drawGenerators();

    image(canvas, 0, 0);
    canvas.endDraw();
  }
}
