package spacefiller.spaceplants.planets;

import spacefiller.math.Vector;
import spacefiller.math.sdf.Circle;
import spacefiller.math.sdf.FloatField2;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Planet {
  public Particle particle;
  public ParticleSystem planetSystem;
  public Circle sdf;
  public Set<ParticleTag> friends;
  private float targetRadius;
  private int tick = 0;

  public Planet(Vector position, float radius, ParticleSystem planetSystem) {
    this.targetRadius = radius;
    this.planetSystem = planetSystem;
    this.particle = this.planetSystem.createParticle(position);
    this.particle.setRadius(radius);
    this.sdf = new Circle(position.x, position.y, radius);
    this.friends = new HashSet<>();
  }

  public void addFriend(ParticleTag tag) {
    this.friends.add(tag);
  }

  public void update() {
    tick++;
    this.sdf.setPosition(particle.getPosition());

    if (tick % 1 == 0) {
      if (this.particle.getRadius() < targetRadius) {
        this.particle.setRadius(this.particle.getRadius() + 1);
      }

      if (this.sdf.getRadius() < targetRadius) {
        this.sdf.setRadius(this.sdf.getRadius() + 1);
      }
    }
  }

  public boolean isFriendly(ParticleTag tag) {
    return friends.contains(tag);
  }

  public void addFriends(ParticleTag[] tags) {
    friends.addAll(Arrays.asList(tags));
  }
}
