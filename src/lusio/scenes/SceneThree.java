package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.*;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class SceneThree extends Scene {
  OscillatorGenerator oscillatorGenerator;
  PerlinLine perlinLine;
  FluidBoxGenerator fluidBoxGenerator;

  @Override
  public void setup(Map<String, Graph> graphs) {
//    oscillatorGenerator = new OscillatorGenerator();
//    oscillatorGenerator.setPos(500, 500);
//    addGenerator(oscillatorGenerator);
//
//    perlinLine = new PerlinLine();
//    perlinLine.setPos(800, 500);
//    addGenerator(perlinLine);

    fluidBoxGenerator = new FluidBoxGenerator();
    fluidBoxGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    fluidBoxGenerator.setIsoThreshold(5);
    fluidBoxGenerator.setWireFrame(false);
    addGenerator(fluidBoxGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    fluidBoxGenerator.setRotation(cube.getQuaternion());
    fluidBoxGenerator.setRestLength(Math.max(cube.getRotationalVelocity(), 1) * 30 + 15);

    // fluidBoxGenerator.setDrawScale(cube.getFlipAmount() * 0.5f + 2);
    // fluidBoxGenerator.setWireFrame(cube.getFlipAmount() > 0.5);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
