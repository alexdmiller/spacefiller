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

import java.util.Map;

public class SceneTwo extends Scene {
  @Override
  public void setup(Map<String, Graph> graphs) {
    ParticleGenerator gen = new ParticleGenerator(50, 10, new Bounds(100));
    gen.setPos(800, 500);
    gen.addRenderer(new ParticleWebRenderer(10, 1));
    generators.add(gen);

    Graph graph = graphs.get("window");
    System.out.println("graph " + graph);
    GraphGenerator graphGen = new GraphGenerator(graph, new SinGraphRenderer());
    generators.add(graphGen);
  }
}
