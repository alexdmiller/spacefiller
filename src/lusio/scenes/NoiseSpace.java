package lusio.scenes;

import com.sun.org.apache.bcel.internal.generic.LUSHR;
import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.Lusio;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import lusio.generators.PerlinFlowGenerator;
import particles.Bounds;
import particles.behaviors.FlockParticles;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class NoiseSpace extends Scene {
  PerlinFlowGenerator perlinFlowGenerator;
  private float scrollSpeed;


  @Override
  public void setup(Map<String, Graph> graphs) {
    perlinFlowGenerator = new PerlinFlowGenerator(new Bounds(Lusio.WIDTH * 2, Lusio.HEIGHT * 2));
    perlinFlowGenerator.setPos(Lusio.WIDTH / 2, Lusio.HEIGHT / 2);
    perlinFlowGenerator.setFallSpeed(0);
    perlinFlowGenerator.setNumPoints(300);
    perlinFlowGenerator.setLineSparsity(0f);
    perlinFlowGenerator.setScrollSpeed(1);
    perlinFlowGenerator.setCircleRadius(300);
    perlinFlowGenerator.setLineThickness(6);
    perlinFlowGenerator.setInterpolation(1);
    perlinFlowGenerator.setNoiseSpeed1(0);
    perlinFlowGenerator.setNoiseSpeed2(0);
    perlinFlowGenerator.setNoiseScale(1000);
    perlinFlowGenerator.setMainSpeed(0.05f);
    perlinFlowGenerator.setScrambleSpeed(0.0001f);
    perlinFlowGenerator.setLineLength(50);
    addGenerator(perlinFlowGenerator);
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    scrollSpeed += cube.getFlipAmount() / 10;
    scrollSpeed *= 0.9f;

    float[] euler = cube.getEulerRotation();

    perlinFlowGenerator.setScrollSpeed(scrollSpeed);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 5000);
    perlinFlowGenerator.setNoiseSpeed2(euler[0] / 1000);
    perlinFlowGenerator.setFlowForce(scrollSpeed * 10 + 2);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
