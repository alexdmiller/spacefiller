package lusio.scenes;

import graph.Graph;
import graph.PipeGraphRenderer;
import graph.SinGraphRenderer;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import particles.Bounds;
import particles.Particle;
import particles.renderers.ParticleDotRenderer;
import particles.renderers.ParticleWebRenderer;
import processing.core.PGraphics;
import toxi.geom.Quaternion;

import java.util.Map;

public class SceneTwo extends Scene {
  ParticleGenerator particleGenerator;

  @Override
  public void setup(Map<String, Graph> graphs) {
    particleGenerator = new ParticleGenerator(50, new Bounds(100));
    particleGenerator.setPos(800, 500);
    particleGenerator.addRenderer(new ParticleWebRenderer(10, 1));
    addGenerator(particleGenerator);

    Graph graph = graphs.get("window");
    System.out.println("graph " + graph);
    GraphGenerator graphGen = new GraphGenerator(graph, new SinGraphRenderer());
    addGenerator(graphGen);
  }

  @Override
  public void draw(Quaternion quaternion, PGraphics graphics) {
    particleGenerator.setRotation(quaternion);
    super.draw(quaternion, graphics);
  }

  @Override
  public boolean transitionOut() {
    return true;
  }
}
