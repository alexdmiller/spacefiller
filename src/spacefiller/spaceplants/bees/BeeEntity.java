package spacefiller.spaceplants.bees;

import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.math.Vector;
import spacefiller.particles.*;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.plants.PlantNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BeeEntity {
  public static final float AWAKE_SPRING_LENGTH = 3f;
  public static final float ASLEEP_SPRING_LENGTH = 0.1f;

  private static int uid = 0;

  private ParticleSystem particleSystem;
  private List<Particle> particles;
  private List<Spring> springs;
  private Particle head;
  private Hive motherHive;
  private Hive currentHive;
  private Mode mode;
  private BeeSystem beeSystem;
  private boolean captured;
  private Particle target;
  private float lightLevel;
  private int life;
  private int team;
  private BeeColor color;
  private int beeSegments;

  enum Mode {
    BABY, FLOCKING, GOING_HOME, SEARCH_FOR_FOOD, SLEEPING, CARRY_PARTICLE, STRUGGLING_FOR_SURVIVAL, ESCAPE_HIVE
  }

  public BeeEntity(ParticleSystem particleSystem, float x, float y, Hive motherHive, BeeSystem beeSystem) {
    this.beeSystem = beeSystem;
    this.particleSystem = particleSystem;
    particles = new ArrayList<>();
    springs = new ArrayList<>();
    this.motherHive = motherHive;
    this.team = motherHive.getIndex();
    this.color = motherHive.getColor();
    captured = false;
    this.beeSegments = 3;

    uid++;

    Particle last = particleSystem.createParticle(x, y);
    head = last;
    last.addTag(ParticleTag.HEAD);
    last.addTag(ParticleTag.BEE);
    last.addTag(ParticleTag.BABY);
    last.setTeam(team);
    last.setUserData("bee",this);
    particles.add(last);

    mode = Mode.BABY;
  }

  public BeeEntity(ParticleSystem particleSystem, float x, float y, BeeSystem beeSystem, int team, BeeColor color, int beeSegments) {
    this.beeSystem = beeSystem;
    this.particleSystem = particleSystem;
    particles = new ArrayList<>();
    springs = new ArrayList<>();
    this.team = team;
    this.color = color;
    captured = false;
    this.beeSegments = beeSegments;

    uid++;

    Particle last = particleSystem.createParticle(x, y);
    head = last;
    last.addTag(ParticleTag.HEAD);
    last.addTag(ParticleTag.BEE);
    last.addTag(ParticleTag.BABY);
    last.setTeam(team);
    last.setUserData("bee",this);
    particles.add(last);

    mode = Mode.BABY;
  }

  public void destroyParticles() {
    particles.forEach(p -> p.setRemoveFlag(true));
  }

  public void removeSelf() {
    beeSystem.removeBee(this);
  }

  public void dampen(float a) {
    particles.forEach(p -> p.getVelocity().mult(a));
  }

  public void draw(PGraphics graphics) {
    graphics.stroke(color.getWormColor());
    graphics.strokeWeight(2);
    graphics.noFill();

    graphics.blendMode(PConstants.BLEND);
    if (particles.size() == 1) {
      graphics.point(particles.get(0).getPosition().x, particles.get(0).getPosition().y);
    } else {
      graphics.beginShape(PConstants.LINE_STRIP);
      for (Particle p : particles) {
        graphics.vertex(p.getPosition().x, p.getPosition().y);
      }
      graphics.endShape();
    }
  }

  public void update(float lightLevel) {
    life++;

    this.lightLevel = lightLevel;

    Particle closest = head.findClosestNeighbor(ParticleTag.FLOWER);
    if (closest == null) {
      closest = head.findClosestNeighbor(ParticleTag.DEAD_FLOWER);
    }

    if (life > Params.i(PName.WORM_LIFETIME) ||
        (head.hasUserData("kill") && (boolean) head.getUserData("kill"))) {
      dropFood();
      removeSelf();
      return;
    }

    updateState(lightLevel, closest);
  }

  public void dropFood() {
    if (mode == Mode.CARRY_PARTICLE && target != null) {
      target.addTag(ParticleTag.GLOBAL_REPEL);
      target.addTag(ParticleTag.DEAD_FLOWER);
      target.setUserData("dropped", true);
      target.setInternalFriction(0.9f);
      target.setActive(true);

      target.setRemoveFlag(true);

      target = null;
    }
  }

  public void fallInto(Particle target, float amount) {
    for (Particle p : particles) {
      Vector delta = Vector.sub(p.getPosition(), target.getPosition());
      float dist = delta.magnitude();
      if (dist > 0) {
        delta.normalize();
        delta.mult(-amount * dist / (dist + 4.0f));
        p.getVelocity().add(delta);
      }
    }
  }

  public boolean isCaptured() {
    return captured;
  }

  private void updateState(float lightLevel, Particle closestFlower) {
    switch (mode) {
      case BABY:
        if (head.getLife() > particles.size() * Params.i(PName.BEE_GROWTH_TIMER) && Rnd.random.nextDouble() < 0.5) {
          Particle last = particles.get(particles.size() - 1);

          Particle next = particleSystem.createParticle(
                  (float) (last.getPosition().x + Rnd.random.nextDouble() * 3 - 1.5f),
                  (float) (last.getPosition().y + Rnd.random.nextDouble() * 3 - 1.5f));

          next.addTag(ParticleTag.BEE);
          next.addTag(ParticleTag.BABY);
          next.addTag(ParticleTag.GLOBAL_REPEL);
          next.setTeam(team);
          next.setUserData("bee",this);
          particles.add(next);
          springs.add(particleSystem.createSpring(last, next, 0.5f, 0.1f));

          if (particles.size() == beeSegments) {
            particles.forEach(p -> {
              p.removeTag(ParticleTag.BABY);
              p.addTag(ParticleTag.GLOBAL_REPEL);
            });
            head.addTag(ParticleTag.BEE_FLOCK);
            mode = Mode.FLOCKING;
          }
        }
        break;
      case FLOCKING: {
          Stream<Particle> neighbors = head.getNeighbors(ParticleTag.DANGEROUS_MOUTH);
          neighbors.forEach(n -> {
            head.applyForce(ParticleUtils.getAvoidanceVector(head, n.getPosition(), 5, 0.5f));
          });

          synchronized (beeSystem.getHives()) {
            Optional<Hive> insideHive = beeSystem
                .getHives()
                .stream()
                .filter(hive -> hive.getPosition().dist(head.getPosition()) < hive.getRadius())
                .findFirst();

            if (insideHive.isPresent()) {
              // inside of a hive when I should be flocking; try to escape
              mode = Mode.ESCAPE_HIVE;
              currentHive = insideHive.get();
            } else if (closestFlower != null
                && motherHive != null
                && motherHive.getFoodCount() < Params.i(PName.MAX_HIVE_FOOD)
                && Rnd.random.nextDouble() < Params.f(PName.GRAB_FLOWER_CHANCE)) {
              mode = Mode.SEARCH_FOR_FOOD;
              target = closestFlower;
            }  else if (lightLevel < Params.f(PName.NIGHT_THRESHOLD) && Rnd.random.nextDouble() < 0.01) {
              // go to sleep
              if (particleSystem.getNeighbors(head.getPosition(), ParticleTag.BEE)
                  .filter(particle -> particle.getTeam() == head.getTeam())
                  .count() > 10) {
                mode = Mode.SLEEPING;
                head.removeTag(ParticleTag.BEE_FLOCK);
                head.addTag(ParticleTag.BEE_HUDDLE);
                springs.forEach(s -> s.setSpringLength(ASLEEP_SPRING_LENGTH));
              }
            }
          }


        }
        break;
      case SEARCH_FOR_FOOD:
        // if our target still has the flower tag, that means another bee
        // hasn't gotten to it yet
        if ((target.hasTag(ParticleTag.FLOWER) || target.hasTag(ParticleTag.DEAD_FLOWER)) && !target.isRemoveFlag()) {
          head.seek(target.getPosition(), 5, 0.5f);

          // eat a flower
          if (head.getPosition().dist(target.getPosition()) < 3) {
            PlantNode n = (PlantNode) target.getUserData("plant_node");
            n.detach();
            mode = Mode.CARRY_PARTICLE;
            target.setActive(false);
            target.removeTag(ParticleTag.PLANT);
            target.removeTag(ParticleTag.FLOWER);
            target.removeTag(ParticleTag.DEAD_FLOWER);
            target.setUserData("touched", target.hasUserData("touched") ? (int) target.getUserData("touched") + 1 : 1);
            head.removeTag(ParticleTag.BEE_FLOCK);
          }
        } else {
          // another bee has already eaten the flower, so give up
          mode = Mode.FLOCKING;
          target = null;
        }
        break;
      case CARRY_PARTICLE: {
          if (motherHive.isDead()) {
            dropFood();
            mode = Mode.FLOCKING;
            break;
          }

          particleSystem.setPosition(target, head.getPosition());

          head.seek(motherHive.getPosition(), 10f, 1f);

          float distanceToHive = head.getPosition().dist(motherHive.getPosition());

          // enter the hive
          if (head.getVelocity().magnitude() < 0.1f && Rnd.random.nextDouble() < 0.1) {
            Vector delta = Vector.sub(motherHive.getPosition(), head.getPosition());
            delta.normalize();
            delta.mult(3f);
            head.applyForce(delta);

          // drop flower off
          } else if (distanceToHive < 5) {
            target.setInternalFriction(0.8f);
            target.addTag(ParticleTag.HIVE_FOOD);
            target.removeTag(ParticleTag.GLOBAL_REPEL);
            mode = Mode.ESCAPE_HIVE;
            motherHive.addFood((PlantNode) target.getUserData("plant_node"));
            target.setActive(true);
            target = null;
            currentHive = motherHive;
          }
        }
        break;
      case ESCAPE_HIVE:
        head.avoid(currentHive.getPosition(), 2f);
        float distanceToHive = head.getPosition().dist(currentHive.getPosition());

        // push through wall
        if (Rnd.random.nextDouble() < 0.1) {
          Vector delta = Vector.sub(head.getPosition(), currentHive.getPosition());
          delta.normalize();
          head.applyForce(delta);
        }

        // out of hive; flock like normal
        if (distanceToHive > currentHive.getRadius() + 20) {
          mode = Mode.FLOCKING;
          head.addTag(ParticleTag.BEE_FLOCK);
          currentHive = null;
        }
        break;
      case SLEEPING: {
          Stream<Particle> neighbors = head.getNeighbors(ParticleTag.DANGEROUS_MOUTH);
          Stream<Particle> foodNeighbors = head.getNeighbors(ParticleTag.FLOWER);

          // wake up
          if (foodNeighbors.count() > 0 || neighbors.count() > 0 || (lightLevel > Params.f(PName.NIGHT_THRESHOLD) && Rnd.random.nextDouble() < 0.1f)) {
            mode = Mode.FLOCKING;
            head.addTag(ParticleTag.BEE_FLOCK);
            head.removeTag(ParticleTag.BEE_HUDDLE);
            springs.forEach(s -> s.setSpringLength(AWAKE_SPRING_LENGTH));
          }
        }
        break;
      case STRUGGLING_FOR_SURVIVAL:
        if (target != null) {
          target.addTag(ParticleTag.GLOBAL_REPEL);
          target.setInternalFriction(0.8f);
          target.setActive(true);
          target = null;
        }
        head.getVelocity().add(Vector.random2D().mult(0.6f));
    }
  }
}
