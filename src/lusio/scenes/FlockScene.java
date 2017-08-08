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
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.Arrays;
import java.util.Map;

public class FlockScene extends Scene {
  ParticleGenerator particleGenerator;
  FlockParticles flockParticles;
  ParticleWebRenderer particleWebRenderer;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(50, new Bounds(Lusio.WIDTH, Lusio.HEIGHT), 2);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    particleGenerator.addRenderer(new ParticleWormRenderer(20, 3));

    particleWebRenderer = new ParticleWebRenderer(50, 2);
    particleGenerator.addRenderer(particleWebRenderer);
    particleGenerator.addRenderer(new ParticleDotRenderer(5));
    particleGenerator.getParticleSystem().setMaxParticles(300);
    particleGenerator.getParticleSystem().createSource(0, 0, 1, 2);

    flockParticles = new FlockParticles(1, 0.5f, 0.5f, 40, 100, 100, 1f, 5);

    particleGenerator.addBehavior(flockParticles);
    addGenerator(particleGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    float[] euler = cube.getNormalizedEuler();

    flockParticles.setMaxSpeed(cube.getFlipAmount() * 20 + 5);
    flockParticles.setDesiredSeparation(euler[1] * 100 + 10);

//    flockParticles.setDesiredSeparation(euler[0] * 100 + 4);
    //flockParticles.setCohesionThreshold(euler[2] * 100);
//    flockParticles.setCohesionThreshold(euler[2]);
    particleWebRenderer.setLineThreshold(euler[0] * 100);
    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
