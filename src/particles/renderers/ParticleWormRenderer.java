package particles.renderers;

import boids.Boid;
import boids.renderers.BoidRenderer;
import com.google.common.collect.EvictingQueue;
import particles.Particle;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/28/17.
 */
public class ParticleWormRenderer implements ParticleRenderer {
  private List<IndividualWormRenderer> individualWormRenderers;

  public ParticleWormRenderer() {
    individualWormRenderers = new ArrayList<>();
  }

  @Override
  public void render(PGraphics graphics, List<Particle> particles) {
    for (IndividualWormRenderer r : individualWormRenderers) {
      r.draw(graphics);
    }
  }

  @Override
  public void particleAdded(Particle particle) {
    individualWormRenderers.add(new IndividualWormRenderer(particle));
  }

  @Override
  public void particleRemoved(Particle particle) {

  }

  public class IndividualWormRenderer {
    public static final int HISTORY_SIZE = 30;
    private EvictingQueue<PVector> history;
    private boolean markedForDeath;
    private boolean readyToDie;
    private Particle particle;

    public IndividualWormRenderer(Particle particle) {
      this.history = EvictingQueue.create(HISTORY_SIZE);
      this.particle = particle;
    }

    public void markReadyForDeath() {
      markedForDeath = true;
    }

    public void draw(PGraphics graphics) {

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
