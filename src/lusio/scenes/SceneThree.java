package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
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
    fluidBoxGenerator.setPos(800, 500);
    addGenerator(fluidBoxGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    fluidBoxGenerator.setRotation(cube.getQuaternion());

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
