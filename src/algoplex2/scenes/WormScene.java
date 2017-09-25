package algoplex2.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import com.sun.tools.internal.jxc.ap.Const;
import common.color.ConstantColorProvider;
import common.PerlinVectorField;
import common.VectorField;
import lusio.components.ParticleComponent;
import particles.behaviors.*;
import spacefiller.remote.Mod;
import particles.Bounds;
import particles.renderers.*;
import processing.core.PGraphics;
import processing.core.PVector;

public class WormScene extends GridScene {
  private ParticleComponent particleGenerator;

  @Mod
  public FlockParticles flockParticles = new FlockParticles(1, 1f, 0.5f, 15, 40, 40, 1f, 2);
  @Mod
  public RepelFixedPoints repelFixedPoints = new RepelFixedPoints(50, 0.01f);;

  private float t;

  @Override
  public void preSetup(Grid grid) {
    super.preSetup(grid);

    particleGenerator = ParticleComponent.withFatalBounds(500, new Bounds(grid.getWidth(), grid.getHeight()), 2);
    particleGenerator.setPos(grid.getWidth() / 2, grid.getHeight() / 2);
    particleGenerator.addBehavior(flockParticles);
    particleGenerator.addBehavior(new WiggleParticles());

    particleGenerator.getParticleSystem().createSource(0, 0, 1, 2);

    ParticleRenderer particleRenderer = new ParticleWormRenderer(10, 2, ConstantColorProvider.WHITE);
    particleGenerator.addRenderer(particleRenderer);

    addComponent(particleGenerator);

    super.setup();
  }

  @Override
  public void draw(PGraphics   graphics) {
    t += 0.1f;

    super.draw(graphics);
  }
}
