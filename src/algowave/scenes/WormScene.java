package algowave.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import algoplex2.scenes.GridScene;
import common.color.ConstantColorProvider;
import lusio.components.ParticleComponent;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.behaviors.FollowPaths;
import particles.behaviors.RepelFixedPoints;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.Scene;
import spacefiller.remote.Mod;

public class WormScene extends Scene {
  private ParticleComponent particleGenerator;

  @Mod
  public FlockParticles flockParticles = new FlockParticles(2, 1f, 1f, 50, 100, 100, 0.5f, 2);

  @Mod
  public FollowPaths followPaths;

  @Mod
  public ParticleWebRenderer particleWebRenderer;

  @Mod
  public RepelFixedPoints repelFixedPoints = new RepelFixedPoints(50, 0.01f);;

  private ParticleWormRenderer particleWormRenderer;

  private float t;

  @Override
  public void setup() {
    particleGenerator = ParticleComponent.withFatalBounds(200, new Bounds(width, height), 2);
    particleGenerator.setPos(width / 2, height / 2);
    particleGenerator.addBehavior(flockParticles);

//    followPaths = new FollowPaths();
//
//    for (Edge e : grid.getEdges()) {
//      PVector offset = new PVector(grid.getWidth() / 2, grid.getHeight() / 2);
//      followPaths.addPathSegment(PVector.sub(e.n1.position, offset), PVector.sub(e.n2.position, offset));
//    }
//
//    followPaths.maxForce = 1;
//    followPaths.maxSpeed = 4;
//    followPaths.radius = 7;
    // particleGenerator.addBehavior(followPaths);


//    PerlinVectorField perlinVectorField = new PerlinVectorField(10);
//    FlowParticles flowParticles = new FlowParticles(perlinVectorField);
//    flowParticles.maxForce = 5;
//    particleGenerator.addBehavior(flowParticles);

    particleGenerator.getParticleSystem().createSource(0, 0, 1, 2);

    particleWebRenderer = new ParticleWebRenderer(100, 1);
    particleGenerator.addRenderer(particleWebRenderer);

//    ParticleDotRenderer particleDotRenderer = new ParticleDotRenderer(5);
//    particleGenerator.addRenderer(particleDotRenderer);

    particleWormRenderer = new ParticleWormRenderer(20, 2, ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleWormRenderer);

    addComponent(particleGenerator);

    super.setup();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += 0.1f;

    // particleWebRenderer.setLineThreshold(flockParticles.desiredSeparation - 5);

    super.draw(graphics);
  }
}
