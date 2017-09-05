package algoplex2.scenes;

import algoplex2.Quad;
import common.color.ConstantColorProvider;
import common.PerlinVectorField;
import common.VectorField;
import lusio.components.ParticleComponent;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.behaviors.FlowParticles;
import particles.behaviors.ParticleFriction;
import particles.behaviors.RepelFixedPoints;
import particles.renderers.*;
import processing.core.PGraphics;
import processing.core.PVector;

public class ParticleScene extends GridScene {
  private ParticleComponent particleGenerator;
  private FlockParticles flockParticles;
  private RepelFixedPoints repelFixedPoints;
  private VectorField flowField;
  private FlowParticles flowParticles;
  private ParticleWebRenderer particleWebRenderer;
  private float t;

  @Override
  public void setup() {
    particleGenerator = new ParticleComponent(500, new Bounds(grid.getWidth(), grid.getHeight()), 2);
    particleGenerator.setPos(grid.getWidth() / 2, grid.getHeight() / 2);

    ParticleRenderer particleRenderer = new ParticleDotRenderer(2, ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleRenderer);

    particleWebRenderer = new ParticleWebRenderer(20, 1);
    particleWebRenderer.setColorProvider(ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleWebRenderer);

    ParticleWormRenderer particleWormRenderer = new ParticleWormRenderer(20, 1, ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleWormRenderer);

    flockParticles = new FlockParticles(1, 1f, 0.5f, 10, 20, 30, 1f, 5);
    particleGenerator.addBehavior(flockParticles);

    repelFixedPoints = new RepelFixedPoints(50, 0.01f);
    PVector shift = new PVector(grid.getWidth() / 2, grid.getHeight() / 2);
    for (Quad square : grid.getSquares()) {
      repelFixedPoints.addFixedPoint(PVector.sub(square.getCenter().position, shift));
    }
    particleGenerator.addBehavior(repelFixedPoints);
    particleGenerator.addBehavior(new ParticleFriction(0.7f));

    flowField = new PerlinVectorField(50);
    flowParticles = new FlowParticles(flowField);
    flowParticles.setWeight(0.5f);
    flowParticles.setMaxForce(1);
    particleGenerator.addBehavior(flowParticles);

    addComponent(particleGenerator);

    super.setup();
  }

  @Override
  public void draw(PGraphics   graphics) {
    t += 0.1f;

    repelFixedPoints.setRepelStrength(controller.getValue(0) * 0.25f);
    repelFixedPoints.setRepelThreshold(controller.getValue(1) * 150);

    flockParticles.setDesiredSeparation(controller.getValue(2) * 100);
    particleWebRenderer.setLineThreshold(flockParticles.getDesiredSeparation());

    flowParticles.setWeight(controller.getValue(3) * 1);
    float t = controller.getValue(4) * 10;
    flowParticles.setT(t);

    super.draw(graphics);
  }
}
