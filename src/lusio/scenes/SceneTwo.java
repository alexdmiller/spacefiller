package lusio.scenes;

import graph.Graph;
import graph.SinGraphRenderer;
import lusio.Lightcube;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import lusio.generators.PerlinFlowGenerator;
import particles.Bounds;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class SceneTwo extends Scene {
  ParticleGenerator particleGenerator;
  PerlinFlowGenerator perlinFlowGenerator;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(50, new Bounds(200));
    particleGenerator.setPos(800, 800);
    particleGenerator.addRenderer(new ParticleWebRenderer(100, 1));
//    addGenerator(particleGenerator);

    perlinFlowGenerator = new PerlinFlowGenerator(new Bounds(1000));
    perlinFlowGenerator.setPos(900, 500);
    perlinFlowGenerator.setFallSpeed(0.5f);
    perlinFlowGenerator.setNumPoints(100);
    perlinFlowGenerator.setLineSparsity(1);
    addGenerator(perlinFlowGenerator);

    Graph graph = graphs.get("window");
    if (graph != null) {
      GraphGenerator graphGen = new GraphGenerator(graph, new SinGraphRenderer());
      addGenerator(graphGen);
    }
  }

  @Override
  public void draw(Lightcube cube, PGraphics graphics) {
    particleGenerator.setRotation(cube.getQuaternion());

    perlinFlowGenerator.setInterpolation((float) (cube.getEulerRotation()[0] / Math.PI));
    perlinFlowGenerator.setFlowForce(Math.abs(cube.getEulerRotation()[1]) * 5);
    perlinFlowGenerator.setNoiseScale(1000 - Math.abs(cube.getEulerRotation()[2]) * 200);
    perlinFlowGenerator.setNoiseSpeed1(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setNoiseSpeed2(cube.getRotationalVelocity() / 1000 + 0.01f);
    perlinFlowGenerator.setLineLength(cube.flipAmount() * 200);

    super.draw(cube, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
