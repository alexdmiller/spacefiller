package particles.renderers;

import boids.Boid;
import boids.renderers.BoidRenderer;
import com.google.common.collect.EvictingQueue;
import lusio.Lusio;
import particles.Particle;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/28/17.
 */
public class ParticleWormRenderer extends ParticleRenderer {
  private List<IndividualWormRenderer> individualWormRenderers;
  private int wormLength;
  private float lineThickness;

  public ParticleWormRenderer(int wormLength, float lineThickness) {
    individualWormRenderers = new ArrayList<>();
    this.wormLength = wormLength;
    this.lineThickness = lineThickness;
  }

  @Override
  public void render(PGraphics graphics) {
    int i = 0;
    for (IndividualWormRenderer r : individualWormRenderers) {
      graphics.stroke(Lusio.instance.getColor(i));
      r.draw(graphics);
      i++;
    }

  }

  public void setParticles(List<Particle> particles) {
    for (Particle p : particles) {
      particleAdded(p);
    }
    super.setParticles(particles);
  }

  @Override
  public void particleAdded(Particle particle) {
    individualWormRenderers.add(new IndividualWormRenderer(particle));
  }

  public void particleRemoved(Particle particle) {
    // TODO
  }

  public class IndividualWormRenderer {
    private EvictingQueue<PVector> history;
    private boolean markedForDeath;
    private boolean readyToDie;
    private Particle particle;

    public IndividualWormRenderer(Particle particle) {
      this.history = EvictingQueue.create(wormLength);
      this.particle = particle;
    }

    public void markReadyForDeath() {
      markedForDeath = true;
    }

    public void draw(PGraphics graphics) {
      graphics.strokeWeight(lineThickness);
      if (markedForDeath) {
        if (!history.isEmpty()) {
          history.remove();
        }
        if (history.size() == 0) {
          readyToDie = true;
        }
      } else {
        history.add(particle.position.copy());
      }
      PVector last = null;
      for (PVector p : history) {
        if (last != null) {
          graphics.line(last.x, last.y, last.z, p.x, p.y, p.z);
        }
        last = p;
      }
    }

    public void clear() {
      history.clear();
    }
  }

}
