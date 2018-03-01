package algowave.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import algoplex2.scenes.GridScene;
import common.ConstantVectorField;
import common.PerlinVectorField;
import common.color.ConstantColorProvider;
import lusio.components.ParticleComponent;
import particles.Bounds;
import particles.Particle;
import particles.behaviors.FlockParticles;
import particles.behaviors.FlowParticles;
import particles.behaviors.FollowPaths;
import particles.behaviors.RepelFixedPoints;
import particles.renderers.DelaunayRenderer;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import particles.renderers.ParticleWormRenderer;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.Scene;
import spacefiller.remote.Mod;

public class WormScene extends Scene {
  private ParticleComponent particleGenerator;

  @Mod
  public FlockParticles flockParticles = new FlockParticles(1f, 0.3f, 0.3f, 30, 50, 50, 0.2f, 1f);

  @Mod
  public FollowPaths followPaths;

  @Mod
  public ParticleWebRenderer particleWebRenderer;

  @Mod
  public float waterSpeed = 0.2f;

  @Mod
  public RepelFixedPoints repelFixedPoints = new RepelFixedPoints(50, 1);

  private ParticleWormRenderer particleWormRenderer;

  PerlinVectorField perlinVectorField;
  FlowParticles flowParticles;
  private float t;

  @Mod
  public float noiseScroll = 0.1f;

  @Override
  public void setup() {
    particleGenerator = ParticleComponent.withDonutBounds(1000, new Bounds(width, height), 2);
    particleGenerator.setPos(width / 2, height / 2);
    particleGenerator.addBehavior(flockParticles);
//
//    for (int i = 0; i < 20; i++) {
//      repelFixedPoints.addFixedPoint(new PVector((float) Math.random() * width - width/2, (float) Math.random() * height - height / 2));
//    }
//    particleGenerator.addBehavior(repelFixedPoints);


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


    perlinVectorField = new PerlinVectorField(100);
    flowParticles = new FlowParticles(perlinVectorField);
    flowParticles.maxForce = 0.2f;
    flowParticles.weight = 0.2f;
    particleGenerator.addBehavior(flowParticles);
//
//    ConstantVectorField constantField = new ConstantVectorField(new PVector(1, 0));
//    FlowParticles flowRight = new FlowParticles(constantField);
//    flowRight.maxForce = 1;
//    flowRight.weight = 0.5f;
//    particleGenerator.addBehavior(flowRight);


    particleGenerator.getParticleSystem().createSource(0, 0, 1, 2);

//    particleWebRenderer = new ParticleWebRenderer(100, 1);
//    particleWebRenderer.setColorProvider(new ConstantColorProvider(0xFF666666));
//    particleGenerator.addRenderer(particleWebRenderer);

//    ParticleDotRenderer particleDotRenderer = new ParticleDotRenderer(5);
//    particleGenerator.addRenderer(particleDotRenderer);

    particleWormRenderer = new ParticleWormRenderer(10, 1, ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleWormRenderer);

    addComponent(particleGenerator);

    super.setup();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += noiseScroll;

    for (Particle p : particleGenerator.getParticleSystem().getParticles()) {
      p.position.x -= waterSpeed;
    }

    flowParticles.x = t;

    //particleWebRenderer.setLineThreshold(flockParticles.desiredSeparation + 2);

    super.draw(graphics);
  }
}
