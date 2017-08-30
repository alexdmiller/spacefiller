package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Quad;
import color.ColorProvider;
import color.ConstantColorProvider;
import graph.*;
import lightcube.Lightcube;
import lusio.Lusio;
import lusio.components.ContourComponent;
import lusio.components.GraphComponent;
import lusio.components.ParticleComponent;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.GraphParticleRenderer;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleRenderer;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import scene.Scene;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import java.awt.*;
import java.util.Map;

public class ParticleScene extends GridScene {
  private float t = 0;
  private ParticleComponent particleGenerator;
  private FlockParticles flockParticles;


  public ParticleScene() {
    fitToGrid();
  }

  @Override
  public void setup() {
    particleGenerator = new ParticleComponent(50, new Bounds(Lusio.WIDTH, Lusio.HEIGHT), 2);
    particleGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);

    ParticleRenderer particleRenderer = new ParticleDotRenderer(10, new ConstantColorProvider(0xFFFFFFFF));
    particleGenerator.addRenderer(particleRenderer);
    particleGenerator.addRenderer(new ParticleDotRenderer(20, Lusio.instance));
    super.setup();
  }

  @Override
  public void draw(PGraphics graphics) {

    super.draw(graphics);
  }
}
