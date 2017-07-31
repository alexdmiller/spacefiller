package particles.renderers;

import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public interface ParticleRenderer {
  void render(PGraphics graphics, List<Particle> particles);
  void particleAdded(Particle particle);
  void particleRemoved(Particle particle);
}
