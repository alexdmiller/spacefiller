package lusio.scenes;

import graph.*;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import lusio.generators.PerlinFlowGenerator;
import particles.Bounds;
import particles.behaviors.AttractParticles;
import particles.behaviors.FlockParticles;
import particles.behaviors.ParticleFriction;
import particles.behaviors.RepelParticles;
import particles.renderers.DelaunayRenderer;
import particles.renderers.GraphParticleRenderer;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.nio.channels.Pipe;
import java.util.Map;

public class FancyParticles extends Scene {
  ParticleGenerator particleGenerator;
  FlockParticles flockParticles;
  GraphParticleRenderer graphParticleRenderer;
  DottedLineGraphRenderer dottedLineGraphRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(50, new Bounds(Lusio.WIDTH, Lusio.HEIGHT), 2);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);

    dottedLineGraphRenderer = new DottedLineGraphRenderer();
    dottedLineGraphRenderer.setThickness(4);
    graphParticleRenderer =  new GraphParticleRenderer(dottedLineGraphRenderer);
    particleGenerator.addRenderer(graphParticleRenderer);
    particleGenerator.addRenderer(new ParticleDotRenderer(20));

//    particleGenerator.addBehavior(new AttractParticles(200, 0.1f));
//    particleGenerator.addBehavior(new RepelParticles(100, 0.5f));
//    particleGenerator.addBehavior(new ParticleFriction(0.95f));

    flockParticles = new FlockParticles(1, 0.5f, 0.5f, 100, 100, 100, 1f, 5);
    particleGenerator.addBehavior(flockParticles);

    addGenerator(particleGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    dottedLineGraphRenderer.setColor(cube.getColor());
    float[] euler = cube.getNormalizedEuler();

    flockParticles.setMaxSpeed(cube.getFlipAmount() * 20 + 5);
    flockParticles.setDesiredSeparation(euler[1] * 100 + 10);
    flockParticles.setCohesionThreshold(euler[0] * 100 + 10);
    // particleWebRenderer.setLineThreshold(Math.min(cube.getRotationalVelocity() * 10, 100));

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
