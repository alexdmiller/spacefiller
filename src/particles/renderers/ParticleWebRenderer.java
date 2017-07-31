package particles.renderers;

import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public class ParticleWebRenderer implements ParticleRenderer {
  private float lineThreshold;
  private float lineSize;

  public ParticleWebRenderer(float lineThreshold, float lineSize) {
    this.lineThreshold = lineThreshold;
    this.lineSize = lineSize;
  }

  public void render(PGraphics graphics, List<Particle> particles) {
    graphics.strokeWeight(lineSize);
    for (Particle p1 : particles) {
      for (Particle p2 : particles) {
        float dist = p1.position.dist(p2.position);
        if (dist < lineThreshold) {
          graphics.line(
              p1.position.x, p1.position.y, p1.position.z,
              p2.position.x, p2.position.y, p2.position.z);
        }
      }
    }
  }

  @Override
  public void particleAdded(Particle particle) {

  }

  @Override
  public void particleRemoved(Particle particle) {

  }

  public void setLineThreshold(float lineThreshold) {
    this.lineThreshold = lineThreshold;
  }
}
