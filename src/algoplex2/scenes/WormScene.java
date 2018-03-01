package algoplex2.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import common.StoredVectorField;
import common.color.ConstantColorProvider;
import common.PerlinVectorField;
import common.VectorField;
import graph.Edge;
import lusio.components.ParticleComponent;
import particles.Source;
import particles.behaviors.*;
import spacefiller.remote.Mod;
import particles.Bounds;
import particles.renderers.*;
import processing.core.PGraphics;
import processing.core.PVector;

public class WormScene extends GridScene {
  private ParticleComponent particleGenerator;

  @Mod
  public FlockParticles flockParticles = new FlockParticles(2, 1f, 1f, 10, 50, 50, 0.5f, 2);

  @Mod
  public FollowPaths followPaths;

  @Mod
  public ParticleWebRenderer particleWebRenderer;

  @Mod
  public RepelFixedPoints repelFixedPoints = new RepelFixedPoints(50, 0.01f);;

  private ParticleWormRenderer particleWormRenderer;

  private float t;

  @Override
  public void preSetup(Grid grid) {
    super.preSetup(grid);

    particleGenerator = ParticleComponent.withFatalBounds(200, new Bounds(grid.getWidth(), grid.getHeight()), 2);
    particleGenerator.setPos(grid.getWidth() / 2, grid.getHeight() / 2);
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

    PVector shift = new PVector(
        -grid.getWidth() / 2,
        -grid.getHeight() / 2);
    for (Quad square : grid.getSquares()) {
      repelFixedPoints.addFixedPoint(PVector.add(square.getBottomRight().position, shift));

      if (square.getTopLeft().position.x == 0 || square.getTopLeft().position.y == 0) {
        repelFixedPoints.addFixedPoint(PVector.add(square.getTopLeft().position, shift));
      }
    }

    repelFixedPoints.addFixedPoint(PVector.add(grid.getBoundingQuad().getBottomLeft().position, shift));
    repelFixedPoints.addFixedPoint(PVector.add(grid.getBoundingQuad().getTopRight().position, shift));

    particleGenerator.addBehavior(repelFixedPoints);
    repelFixedPoints.repelStrength = 0.01f;
    repelFixedPoints.repelThreshold = 100;

//    PerlinVectorField perlinVectorField = new PerlinVectorField(10);
//    FlowParticles flowParticles = new FlowParticles(perlinVectorField);
//    flowParticles.maxForce = 5;
//    particleGenerator.addBehavior(flowParticles);

    for (Quad quad : grid.getSquares()) {
      particleGenerator.getParticleSystem().createSource(
          quad.getCenter().position.x - grid.getWidth() / 2,
          quad.getCenter().position.y - grid.getHeight() / 2,
          1,
          2);
    }

    particleWebRenderer = new ParticleWebRenderer(50, 1);
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

    particleWebRenderer.setLineThreshold(flockParticles.desiredSeparation - 5);

    super.draw(graphics);
  }
}
