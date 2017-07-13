package lusio;

import codeanticode.syphon.SyphonServer;
import particles.Bounds;
import particles.behaviors.*;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import lusio.generators.ParticleGenerator;
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

    ParticleGenerator gen = new ParticleGenerator(10, 10,
        new Bounds(200, 100, 200));
    gen.setPos(300, 300);
    gen.addRenderer(new ParticleDotRenderer(10));
    gen.addBehavior(new BoundParticles());
    generators.add(gen);

    ParticleGenerator gen2 = new ParticleGenerator(500, 10,
        new Bounds(200, 100, 100));
    gen2.setPos(600, 300);
    gen2.addRenderer(new ParticleDotRenderer(2));
    generators.add(gen2);

    ParticleGenerator gen3 = new ParticleGenerator(200, 10,
        new Bounds(150, 150, 150));
    gen3.setPos(300, 600);
    gen3.addRenderer(new ParticleWebRenderer(20, 1));
    generators.add(gen3);

    ParticleGenerator gen4 = new ParticleGenerator(50, 10,
        new Bounds(100, 200, 100));
    gen4.setPos(600, 600);
    gen4.addRenderer(new ParticleDotRenderer(6));
    gen4.addRenderer(new ParticleWebRenderer(50, 3));
    generators.add(gen4);

    ParticleGenerator gen5 = new ParticleGenerator(50, 10,
        new Bounds(200, 200, 200));
    gen5.setPos(900, 300);
    gen5.addRenderer(new ParticleDotRenderer(6));
    gen5.addRenderer(new ParticleWebRenderer(50, 1));
    gen5.addBehavior(new AttractParticles(100, 0.01f));
    gen5.addBehavior(new RepelParticles(50, 0.2f));
    gen5.addBehavior(new ParticleFriction(0.9f));
    generators.add(gen5);

    ParticleGenerator gen6 = new ParticleGenerator(50, 10,
        new Bounds(200, 200, 200));
    gen6.setPos(900, 600);
    gen6.addRenderer(new ParticleDotRenderer(6));
    gen6.addBehavior(new FlockParticles());
    gen6.addBehavior(new ParticleFriction(0.9f));
    generators.add(gen6);
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
    canvas.noFill();
    canvas.stroke(255);

    drawGenerators();

    image(canvas, 0, 0);
    canvas.endDraw();
  }
}
