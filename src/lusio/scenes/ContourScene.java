package lusio.scenes;

import graph.Graph;
import graph.PipeGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.ContourGenerator;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.behaviors.JitterParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;
import toxi.geom.Quaternion;
import toxi.geom.Vec3D;

import java.util.Arrays;
import java.util.Map;

public class ContourScene extends Scene {
  ContourGenerator contourGenerator;

  private float transitionOutTimer;
  private float transitionDuration = 20;

  @Override
  public void setup(Map<String, Graph> graphs) {
    contourGenerator = new ContourGenerator(new Bounds(2000));
    contourGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    contourGenerator.setRotation(Quaternion.createFromAxisAngle(new Vec3D(-1, 0, 0), 1f));
    contourGenerator.setCellSize(100);
    contourGenerator.setNoiseScale(2);
    contourGenerator.setLineSize(2);
    contourGenerator.setUpdateSpeed(0.001f);


    addGenerator(contourGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    float euler[] = cube.getEulerRotation();
    graphics.pushMatrix();
    graphics.translate(0, -200, 0);
    contourGenerator.setColor(cube.getColor());
    contourGenerator.setXSpeed(euler[0] * -0.1f);
    contourGenerator.setYSpeed(euler[2] * -0.1f);
    contourGenerator.setNoiseAmplitude(cube.getRotationalVelocity() * 10 + 300);

    graphics.perspective();
    super.draw(cube, graphics);
    graphics.popMatrix();
  }
}
