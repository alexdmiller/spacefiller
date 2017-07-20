package lusio.scenes;

import graph.AnimatedFillGraphRenderer;
import graph.Graph;
import graph.PipeGraphRenderer;
import lusio.generators.GraphGenerator;
import lusio.generators.ParticleGenerator;
import particles.Bounds;
import particles.Particle;
import particles.renderers.ParticleDotRenderer;
import processing.core.PGraphics;

import java.util.Map;

public class SceneOne extends Scene {
  @Override
  public void setup(Map<String, Graph> graphs) {
    ParticleGenerator gen = new ParticleGenerator(50, 10, new Bounds(100));
    gen.setPos(500, 500);
    gen.addRenderer(new ParticleDotRenderer(2));
    generators.add(gen);

    Graph graph = graphs.get("window");
    System.out.println("graph " + graph);
    GraphGenerator graphGen = new GraphGenerator(graph, new PipeGraphRenderer());
    generators.add(graphGen);
  }


}
