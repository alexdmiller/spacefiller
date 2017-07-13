package particles.renderers;

import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public class ParticleDotRenderer implements ParticleRenderer {
  private float dotSize;

  public ParticleDotRenderer(float dotSize) {
    this.dotSize = dotSize;
  }

  @Override
  public void render(PGraphics graphics, List<Particle> particles) {
    graphics.strokeWeight(this.dotSize);
    for (Particle p : particles) {
      graphics.point(p.position.x, p.position.y, p.position.z);
    }
  }
}
