package particles.renderers;

import lusio.Lusio;
import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public class ParticleDotRenderer extends ParticleRenderer {
  private float dotSize;

  public ParticleDotRenderer(float dotSize) {
    this.dotSize = dotSize;
  }

  @Override
  public void render(PGraphics graphics) {
    graphics.strokeWeight(this.dotSize);
    int i = 0;
    for (Particle p : particles) {
      graphics.stroke(Lusio.instance.getColor(i));
      graphics.point(p.position.x, p.position.y, p.position.z);
      i++;
    }
  }

  public void setDotSize(float dotSize) {
    this.dotSize = dotSize;
  }
}
