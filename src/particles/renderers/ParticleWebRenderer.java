package particles.renderers;

import lusio.Lusio;
import particles.Particle;
import processing.core.PGraphics;

import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public class ParticleWebRenderer extends ParticleRenderer {
  private float lineThreshold;
  private float lineSize;

  public ParticleWebRenderer(float lineThreshold, float lineSize) {
    this.lineThreshold = lineThreshold;
    this.lineSize = lineSize;
  }

  public void render(PGraphics graphics) {
    graphics.strokeWeight(lineSize);
    int i = 0;
    for (Particle p1 : particles) {
      graphics.stroke(Lusio.instance.getColor(i));
      for (Particle p2 : particles) {
        float dist = p1.position.dist(p2.position);
        if (dist < lineThreshold) {
          graphics.line(
              p1.position.x, p1.position.y, p1.position.z,
              p2.position.x, p2.position.y, p2.position.z);
        }
      }
      i++;
    }
  }

  public void setLineThreshold(float lineThreshold) {
    this.lineThreshold = lineThreshold;
  }
}
