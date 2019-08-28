package particles.renderers;

import spacefiller.color.ColorProvider;
import com.google.common.collect.EvictingQueue;
import particles.Particle;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by miller on 7/28/17.
 */
public class ParticleWormRenderer extends ParticleRenderer {
  private LinkedHashMap<Particle, IndividualWormRenderer> individualWormRenderers;
  private int wormLength;
  private float lineThickness;

  public ParticleWormRenderer(int wormLength, float lineThickness, ColorProvider colorProvider) {
    individualWormRenderers = new LinkedHashMap<>();
    this.wormLength = wormLength;
    this.lineThickness = lineThickness;
    this.colorProvider = colorProvider;
  }

  @Override
  public void render(PGraphics graphics) {
    int i = 0;
    for (IndividualWormRenderer r : individualWormRenderers.values()) {
      graphics.stroke(colorProvider.getColor(i));
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
    individualWormRenderers.put(particle, new IndividualWormRenderer(particle));
  }

  public void particleRemoved(Particle particle) {
    individualWormRenderers.remove(particle);
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
      graphics.stroke(255);
      graphics.noFill();
      if (markedForDeath) {
        if (!history.isEmpty()) {
          history.remove();
        }
        if (history.size() == 0) {
          readyToDie = true;
        }
      } else {
        if (particle.hasTeleported()) {
          history.clear();
        }

        history.add(particle.position.copy());
      }

      graphics.beginShape();
      for (PVector p : history) {
        graphics.vertex(p.x, p.y, p.z);
      }
      graphics.endShape();
    }

    public void clear() {
      history.clear();
    }
  }

}
