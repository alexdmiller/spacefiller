package spacefiller.spaceplants.dust;

import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.spaceplants.SPSystem;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;

import java.util.ArrayList;

public class DustSystem implements SPSystem {
  private ParticleSystem particleSystem;
  private ArrayList<Dust> dustParts;
  private float lightLevel;

  public DustSystem(ParticleSystem particleSystem, int dustCount, float width, float height) {
    this.particleSystem = particleSystem;
    particleSystem.addBehavior(new ParticleFriction(0.7f), ParticleTag.DUST);
    this.dustParts = new ArrayList<>();
    for (int i=0; i<dustCount; i++) {
      dustParts.add(new Dust(particleSystem, width, height));
    }
  }

  public DustSystem(ParticleSystem particleSystem, float width, float height) {
    this(particleSystem, 500, width, height);
  }

  @Override
  public void update() {
    dustParts.forEach(Dust::update);
  }

  @Override
  public void draw(PGraphics graphics) {
    dustParts.forEach(d -> d.draw(graphics));
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }

  public float getLightLevel() {
    return lightLevel;
  }
}

class Dust {

  private Particle particle;
  private ParticleSystem particleSystem;
  private float width;
  private float height;
  private float age;
  private float bright;

  public Dust(ParticleSystem particleSystem, float width, float height) {
    this.particleSystem = particleSystem;
    this.width = width;
    this.height = height;
    this.particle = particleSystem.createParticle((float) Rnd.random.nextDouble()*width, (float)Rnd.random.nextDouble()*height);
    this.particle.setMass(1.0f);
    this.particle.addTag(ParticleTag.DUST);
    this.particle.addTag(ParticleTag.GLOBAL_REPEL);
    this.age = (float) Rnd.random.nextDouble()*100.0f;
    this.bright = 1.0f;
  }

  public void draw(PGraphics graphics) {
    graphics.strokeWeight(3f);
    graphics.fill(255, 255);
    graphics.noStroke();
    graphics.rect(this.particle.getPosition().x, this.particle.getPosition().y, 1, 1);
//    graphics.blendMode(PConstants.ADD);
//    graphics.stroke(255);
//    graphics.strokeWeight(1);
//    Stream<Particle> neighbors = particleSystem.getNeighbors(particle.getPosition(), ParticleTag.DUST);
//    neighbors.forEach(n -> {
//      Vector p1 = particle.getPosition();
//      Vector p2 = n.getPosition();
//      float distance = p1.dist(p2);
//      if (p1.dist(p2) < 10) {
//        graphics.stroke((10 - distance) / distance * 255);
//        graphics.line(p1.x, p1.y, p2.x, p2.y);
//      }
//    });
    // graphics.colorMode(PConstants.BLEND);
  }

  public void update() {
//    this.bright = (float) (Math.cos(this.age/(2.0*Math.PI))*0.5+0.5);
//    if (this.bright < 0.001) {
//      this.particleSystem.setPosition(particle, new Vector((float)Rnd.random.nextDouble()*width, (float)Rnd.random.nextDouble()*height));
//      particle.setVelocity(new Vector());
//    }
//    this.age += 0.01;
  }

}
