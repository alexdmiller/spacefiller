package spacefiller.spaceplants.planets;

import spacefiller.math.Vector;
import spacefiller.math.sdf.Circle;
import spacefiller.math.sdf.FloatField2;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;

public class Planet {
  public Particle particle;
  public ParticleSystem planetSystem;
  public Circle sdf;

  public Planet(Vector position, float radius, ParticleSystem planetSystem) {
    this.planetSystem = planetSystem;
    this.particle = this.planetSystem.createParticle(position);
    this.particle.setRadius(radius);
    this.sdf = new Circle(position.x, position.y, radius);
  }

  public void update() {
    this.sdf.setPosition(particle.getPosition());
  }
}
