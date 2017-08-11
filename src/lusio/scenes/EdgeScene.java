package lusio.scenes;

import graph.*;
import lusio.Lightcube;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import lusio.generators.PerlinFlowGenerator;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.nio.channels.Pipe;
import java.util.Map;

public class EdgeScene extends Scene {
  SinGraphRenderer sinGraphRenderer;
  PipeGraphRenderer pipeGraphRenderer;
  AnimatedFillGraphRenderer animatedFillGraphRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    Graph windowGraph = graphs.get("window");
    Graph poleGraph = graphs.get("pole");
    Graph sideGraph = graphs.get("side");

    if (windowGraph != null) {
      sinGraphRenderer = new SinGraphRenderer();
      addGenerator(new GraphGenerator(windowGraph, sinGraphRenderer));
    }

    if (poleGraph != null) {
      pipeGraphRenderer = new PipeGraphRenderer();
      pipeGraphRenderer.setMaxPerEdge(50);
      addGenerator(new GraphGenerator(poleGraph, pipeGraphRenderer));

      BasicGraphRenderer basicGraphRenderer = new BasicGraphRenderer(2);
      addGenerator(new GraphGenerator(poleGraph, basicGraphRenderer));
    }

    if (sideGraph != null) {
      animatedFillGraphRenderer = new AnimatedFillGraphRenderer();
      animatedFillGraphRenderer.setThickness(10);
      addGenerator(new GraphGenerator(sideGraph, animatedFillGraphRenderer));
    }
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    sinGraphRenderer.setSize(cube.getFlipAmount() * 50 + 2);
    sinGraphRenderer.setFreq(cube.getFlipAmount() * 20);
    sinGraphRenderer.setSpeed(cube.getRotationalVelocity() * 0.1f);
    sinGraphRenderer.setColor(cube.getColor());

    pipeGraphRenderer.setDeviation(cube.getFlipAmount() * 10 + 4);
    pipeGraphRenderer.setDotSize(cube.getRotationalVelocity() * 0.1f + 5);
    pipeGraphRenderer.setFreq(100 - cube.getFlipAmount() * 100);
    pipeGraphRenderer.setColor(cube.getColor());

    animatedFillGraphRenderer.setFillSpeed(cube.getRotationalVelocity());

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
