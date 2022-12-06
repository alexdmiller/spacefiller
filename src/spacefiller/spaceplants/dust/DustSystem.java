package spacefiller.spaceplants.dust;

import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.math.Vector;
import spacefiller.spaceplants.SPSystem;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;

import java.util.ArrayList;

public class DustSystem implements SPSystem {
  private ParticleSystem particleSystem;
  private ArrayList<Particle> dustParts;

  public DustSystem(ParticleSystem particleSystem) {
    this.particleSystem = particleSystem;
    particleSystem.addBehavior(new ParticleFriction(0.7f), ParticleTag.DUST);
    this.dustParts = new ArrayList<>();
  }

  public void createDust(Vector position) {
    Particle particle = particleSystem.createParticle(position);
    particle.setMass(1.0f);
    particle.setRadius(2);
    particle.addTag(ParticleTag.DUST);
    particle.addTag(ParticleTag.GLOBAL_REPEL);
    dustParts.add(particle);
  }

  @Override
  public void update() {

  }

  @Override
  public void draw(PGraphics graphics) {
    dustParts.forEach(d -> {
      graphics.fill(255);
      graphics.noStroke();
//      graphics.rect(d.getPosition().x, d.getPosition().y, 1, 1);
      graphics.ellipseMode(PConstants.CENTER);
      graphics.ellipse(d.getPosition().x, d.getPosition().y, d.getRadius(), d.getRadius());
    });
  }

  public int getNumDust() {
    return dustParts.size();
  }
}
