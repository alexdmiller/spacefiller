package spacefiller.spaceplants.bees;

import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.math.Vector;
import spacefiller.particles.*;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.plants.PlantNode;
import spacefiller.spaceplants.plants.PlantSystem;
import spacefiller.spaceplants.plants.SeedNode;

import java.util.ArrayList;
import java.util.List;

public class Hive {

  private int hiveIndex;
  private Vector position;
  private float radius = 0;
  private ParticleSystem particleSystem;
  private List<Particle> particles;
  private List<Particle> outerParticles;
  private List<Particle> innerParticles;
  private List<Spring> springs;
  private List<Spring> outerSprings;
  private List<Spring> innerSprings;

  private List<Particle> particleDeletionQueue;
  private List<Spring> springDeletionQueue;

  private List<PlantNode> capturedFood;

  private PlantSystem plantSystem;
  private BeeSystem beeSystem;
  private float lightLevel;
  private BeeColor color;

  private static final float SPRING_LENGTH = 1f;
  private static final float SPRING_K = 0.3f;

  private boolean hiveBroken = false;
  private int beesCreated = 0;
  private int life;
  private int hiveSize;

  public Hive(int hiveIndex,
              BeeColor color,
              Vector position,
              ParticleSystem system,
              PlantSystem plantSystem,
              BeeSystem beeSystem,
              int hiveSize) {
    this.hiveIndex = hiveIndex;
    this.color = color;
    this.position = position;
    this.particleSystem = system;
    this.capturedFood = new ArrayList<>();
    this.plantSystem = plantSystem;
    this.beeSystem = beeSystem;
    // TODO:
    this.hiveSize = hiveSize;

    createParticles(position, (int) 3, 1, hiveIndex, SPRING_LENGTH, SPRING_K);

    particles.forEach(p -> {
      p.addTag(ParticleTag.HIVE);
      p.addTag(ParticleTag.GLOBAL_REPEL);
    });

    addBabies(Params.i(PName.STARTING_BABIES_PER_HIVE));

    particleDeletionQueue = new ArrayList<>();
    springDeletionQueue = new ArrayList<>();
  }

  private void createParticles(Vector center, int num, float radius, int team, float springLength, float springK) {
    particles = new ArrayList<>();
    springs = new ArrayList<>();
    innerParticles = new ArrayList<>();
    outerParticles = new ArrayList<>();

    innerSprings = new ArrayList<>();
    outerSprings = new ArrayList<>();

    for (int i = 0; i < num; i++) {
      float theta = (float) (2f * Math.PI / num) * i;
      Particle p = particleSystem.createParticle(
          (float) (center.x + Math.cos(theta) * radius),
          (float) (center.y + Math.sin(theta) * radius));

      p.setTeam(team);
      particles.add(p);
      innerParticles.add(p);
    }

    Particle first = particles.get(0);
    Particle last = particles.get(particles.size() - 1);

    innerSprings.add(particleSystem.createSpring(last, first, springLength, springK));

    for (int i = 0; i < particles.size() - 1; i++) {
      Particle n = particles.get(i);
      innerSprings.add(particleSystem.createSpring(n, particles.get(i + 1), springLength, springK));
    }

    springs.addAll(innerSprings);

    for (int i = 0; i < num; i++) {
      float theta = (float) (2f * Math.PI / num) * i;
      Particle n = particles.get(i);
      Particle p = particleSystem.createParticle(
          (float) (center.x + Math.cos(theta) * (radius + 1)),
          (float) (center.y + Math.sin(theta) * (radius + 1)));

      outerSprings.add(particleSystem.createSpring(n, p, springLength, springK));
      outerParticles.add(p);
      particles.add(p);
    }

    springs.addAll(outerSprings);
  }

  public void addBabies(int num) {
    beesCreated += num;
    for (int i = 0; i < num; i++) {
      float angle = (float) (((float) i / num) * Math.PI * 2);
      float r = 5;
      beeSystem.createBee(
          new Vector(
              position.x + Math.cos(angle) * r,
              position.y + Math.sin(angle) * r
          ),
          this
      );
    }
  }

  public Vector getPosition() {
    return position;
  }

  public float getRadius() {
    return radius;
  }

  public void draw(PGraphics graphics) {
    graphics.noFill();
    graphics.pushMatrix();
    graphics.translate(0, 0, -10);
    graphics.stroke(color.getHiveColor());
    graphics.fill(0, 40);
    graphics.strokeWeight(2);

    for (Spring s : innerSprings) {
      graphics.line(
          s.getN1().getPosition().x, s.getN1().getPosition().y,
          s.getN2().getPosition().x, s.getN2().getPosition().y);
    }
    graphics.popMatrix();

    for (Spring s : outerSprings) {
      graphics.line(
          s.getN1().getPosition().x, s.getN1().getPosition().y,
          s.getN2().getPosition().x, s.getN2().getPosition().y
      );
    }

//
//    for (int i = 0; i < outerParticles.size(); i++) {
//      Particle p = outerParticles.get(i);
//      graphics.stroke(Utils.lerpColor(
//          0xffffffff,
//          color.getHiveColor(),
//          (float) ((Math.sin(Utils.getMillis() / 500f + i * 10 + hiveIndex * 20)) * (1 - lightLevel))));
//      graphics.strokeWeight(2);
//      graphics.point(p.getPosition().x, p.getPosition().y);
//    }
  }

  private void applyForce(Vector force) {
    for (Particle p : particles) {
      p.applyForce(force);
    }
  }

  private void applySoftBoundary(float dist, Vector direction) {
    float force = forceFunction(dist);
    if (force > 0) {
      applyForce(direction.mult(force));
    }
  }

  public float forceFunction(float dist) {
    if (dist < 0) {
      return 1;
    } else if (dist < 100) {
      return 4 / (dist + 1);
    } else {
      return 0;
    }
  }

  public void grow() {
    if (Rnd.random.nextDouble() < 0.1) {
      if (innerParticles.size() < hiveSize) {
        int index = (int) (Rnd.random.nextDouble() * (innerParticles.size() - 2)) + 1;

        Particle center = innerParticles.get(index);

        Particle left = innerParticles.get(Math.floorMod(index - 1, innerParticles.size()));
        Particle right = innerParticles.get(Math.floorMod(index + 1, innerParticles.size()));

        Spring spring = center.findConnection(right);
        spring.removeFlag = true;
        springDeletionQueue.add(spring);

        Vector tangent = Vector.sub(right.getPosition(), left.getPosition()).normalize().mult(2f);
        //center.getPosition().sub(tangent);

        Particle newParticle = particleSystem.createParticle(Vector.add(center.getPosition(), tangent));
        newParticle.addTag(ParticleTag.HIVE);
        newParticle.addTag(ParticleTag.GLOBAL_REPEL);

        // add outer particle
        Particle p = particleSystem.createParticle(Vector.add(center.getPosition(), Vector.mult(tangent, 4)));
        p.addTag(ParticleTag.HIVE);
        p.addTag(ParticleTag.GLOBAL_REPEL);
        outerSprings.add(particleSystem.createSpring(newParticle, p, SPRING_LENGTH, SPRING_K));
        outerParticles.add(p);
        particles.add(p);

        Spring s1 = particleSystem.createSpring(center, newParticle, SPRING_LENGTH, SPRING_K);
        Spring s2 = particleSystem.createSpring(newParticle, right, SPRING_LENGTH, SPRING_K);

        innerSprings.add(s1);
        innerSprings.add(s2);
        springs.add(s1);
        springs.add(s2);

        innerParticles.add(index + 1, newParticle);
        particles.add(newParticle);
      } else if (beesCreated < Params.i(PName.MAX_BEES_CREATED)) {
        addBabies(1);
      }
    }
  }

  public void update(float lightLevel) {
    life++;

    int lifetime = capturedFood.size() > 0 ?
        Params.i(PName.HIVE_LIFETIME) : Params.i(PName.HIVE_LIFETIME);

    if (life > lifetime) {
      particles.get((int) (Rnd.random.nextDouble() * particles.size())).setUserData("kill", true);
    }

    if (!hiveBroken) {
       grow();
    } else {
      particles.get((int) (Rnd.random.nextDouble() * particles.size())).setUserData("kill", true);
    }

    this.lightLevel = lightLevel;
    color.setLightLevel(lightLevel);
    //springs.forEach(s -> s.setSpringLength(lightLevel * 5f));

    // compute centroid of particles
    position.set(0, 0);
    particles.forEach(p -> position.add(p.getPosition()));
    position.div(particles.size());

    float farthestDistance = 0;
    for (Particle p : particles) {
      float d = p.getPosition().dist(position);
      if (d > farthestDistance) {
        farthestDistance = d;
      }
    }

    radius = farthestDistance;

    for (Particle p : particles) {
      if (p.hasUserData("kill") && (boolean) p.getUserData("kill")) {
        p.setRemoveFlag(true);
        p.detachSprings();
        particleDeletionQueue.add(p);
        springDeletionQueue.addAll(p.getConnections());
        hiveBroken = true;
      }
    }

    particles.removeAll(particleDeletionQueue);
    innerParticles.remove(particleDeletionQueue);
    outerParticles.remove(particleDeletionQueue);
    springs.removeAll(springDeletionQueue);
    outerSprings.removeAll(springDeletionQueue);
    innerSprings.removeAll(springDeletionQueue);

    particleDeletionQueue.clear();
    springDeletionQueue.clear();

    if (particles.size() == 0) {
      beeSystem.removeHive(this);
      for (PlantNode p : capturedFood) {
        p.destroy();
      }
    }

    // run away from fly traps
    List<Particle> flytraps = particleSystem.getParticlesWithTag(ParticleTag.FLYTRAP_MOUTH);
    for (Particle flytrap : flytraps) {
      Vector delta = Vector.sub(flytrap.getPosition(), position);
      float m = delta.magnitude();
      if (m==0.0) continue;
      delta.mult(-1f / (m * m));
      if (m < Params.f(PName.HIVE_DESIRED_FLYTRAP_DISTANCE)) {
        applyForce(delta);
      }
    }

    // repel from walls
//    Vector topBackLeft = particleSystem.getBounds().getTopBackLeft();
//    Vector bottomFrontRight = particleSystem.getBounds().getBottomFrontRight();
//
//    applySoftBoundary(position.x - topBackLeft.x, new Vector(1, 0));
//    applySoftBoundary(bottomFrontRight.x - position.x, new Vector(-1, 0));
//    applySoftBoundary(position.y - topBackLeft.y, new Vector(0, 1));
//    applySoftBoundary(bottomFrontRight.y - position.y, new Vector(0, -1));

    // repel particles from eachother (makes hive circular)
    for (int i = 0; i < innerParticles.size(); i++) {
      Particle p1 = innerParticles.get(i);
      for (int j = i + 1; j < innerParticles.size(); j++) {
        Particle p2 = innerParticles.get(j);
        Vector delta = Vector.sub(p1.getPosition(), p2.getPosition());
        float mag = delta.magnitude();
        if (mag==0.0) continue;
        delta.mult(0.02f / (mag * mag));
        p1.applyForce(delta);
        p2.applyForce(Vector.mult(delta, -1));
      }
    }

    // each particle is pushed towards the average position of the
    // connected particles. this flattens out the loops.
//    for (Particle p : particles) {
//      Vector avg = new Vector(0, 0);
//      for (Spring s : p.getConnections()) {
//        Particle n2 = s.other(p);
//        avg.add(n2.getPosition());
//      }
//
//      avg.div(p.getConnections().size());
//
//      Vector delta = Vector.sub(avg, p.getPosition());
//      delta.mult((float) Math.sin(Utils.getMillis() / 10000f) * 0.01f);
//      p.applyForce(delta);
//    }

//    Stream<Particle> neighbors = particleSystem.getNeighbors(position, ParticleTag.BEE_FLOCK);
//    neighbors.forEach(p -> p.getAvoidanceVector(position, 2));


    // food is attracted to center of hive
    if (!hiveBroken) {
      for (PlantNode p : capturedFood) {
        Vector delta = Vector.sub(position, p.getParticle().getPosition());
        delta.mult(0.01f);
        p.getParticle().applyForce(delta);
      }
    }

    if (true) { // lightLevel < Params.f(PName.NIGHT_THRESHOLD)) {
      // digest food
      int numBees = particleSystem.getParticlesWithTag(ParticleTag.HEAD).size();
      if (numBees < Params.i(PName.MAX_BEES) && Rnd.random.nextDouble() < Params.f(PName.FOOD_TO_WORM_CHANCE)) {
        if (!capturedFood.isEmpty()) {
          PlantNode f = capturedFood.remove(0);
          f.getParticle().setRemoveFlag(true);
          f.destroy();

          int numNewBees = Math.max(1, Math.round((Params.i(PName.MAX_BEES) - numBees) / 100f));
          for (int i = 0; i < numNewBees; i++) {
            beeSystem.createBee(Vector.add(position, new Vector(Rnd.random.nextDouble() - 0.5, Rnd.random.nextDouble() - 0.5)), this);
          }

          // if there are literally no more plants present, then as an emergency measure,
          // the hive will produce seeds from flowers
          int numPlants = particleSystem.getParticlesWithTag(ParticleTag.PLANT).size();
          if (Rnd.random.nextDouble() < 0.1 && numPlants == 0) {
            SeedNode seed = plantSystem.createSeed(position.copy());
            seed.getParticle().applyForce(Vector.random2D());
          }
        }
      }
    }
  }

  public void addFood(PlantNode food) {
    capturedFood.add(food);
  }

  public int getIndex() {
    return hiveIndex;
  }

  public BeeColor getColor() {
    return color;
  }

  public int getFoodCount() {
    return capturedFood.size();
  }

  public boolean isDead() {
    return particles.size() == 0;
  }
}
