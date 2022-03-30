package spacefiller.spaceplants.bees;

import processing.core.PGraphics;
import spacefiller.spaceplants.System;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.plants.PlantSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeeSystem implements System {
  private static final FlockParticles.Parameters AWAKE_PARAMS =
          new FlockParticles.Parameters(
                  1,
                  2,
                  1,
                  20,
                  20,
                  17,
                  2f,
                  0.1f);

  private static final FlockParticles.Parameters ASLEEP_PARAMS =
          new FlockParticles.Parameters(
                  0.5f,
                  0,
                  3,
                  0,
                  40,
                  10,
                  1,
                  0.1f);

  // underlying particle system for basic particle physics
  private ParticleSystem particleSystem;
  private PlantSystem plantSystem;

  private List<BeeEntity> beeEntities;
  private List<BeeEntity> beeCreationQueue;
  private List<BeeEntity> beeDeletionQueue;

  private List<Hive> hives;
  private List<Hive> hiveDeletionQueue;
  private float lightLevel;

  private FlockParticles flockParticles;
  private FlockParticles sleepFlock;

  private BeeColor[] colors = new BeeColor[] {
      new BeeColor(0xffffff99, 0xff666666, 0xffffff99, 0xff444444),
      new BeeColor(0xffffffcc, 0xff999999, 0xffffffcc, 0xff555555),
  };

  public BeeSystem(ParticleSystem particleSystem, PlantSystem plantSystem) {
    this.particleSystem = particleSystem;
    this.plantSystem = plantSystem;

    hives = new ArrayList<>();
    hiveDeletionQueue = new ArrayList<>();

    beeCreationQueue = new ArrayList<>();
    beeDeletionQueue = new ArrayList<>();
    beeEntities = Collections.synchronizedList(new ArrayList<>());

    flockParticles = new FlockParticles().setParameters(AWAKE_PARAMS);
    flockParticles.setTeamMode(FlockParticles.TeamMode.SAME);
    sleepFlock = new FlockParticles().setParameters(ASLEEP_PARAMS);
    sleepFlock.setTeamMode(FlockParticles.TeamMode.SAME);

    particleSystem.addBehavior(flockParticles, ParticleTag.BEE_FLOCK, ParticleTag.BEE);
    particleSystem.addBehavior(sleepFlock, ParticleTag.BEE_HUDDLE, ParticleTag.BEE_HUDDLE);
    particleSystem.addBehavior(new SteerBounds(5f, 1f, 0.1f), ParticleTag.BEE);
    particleSystem.addBehavior(new ParticleFriction(0.90f), ParticleTag.BEE);

    // TODO: why is this so different from the other version of repel?
    // TODO: create LocalEffect that doesn't take any neighbors (for steering and particle friction)
    // particleSystem.addBehavior(new SymmetricRepel(5, 1), ParticleTag.BEE);
    particleSystem.addBehavior(new RepelParticles(10, 0.8f), ParticleTag.BEE, ParticleTag.PLANT);

    //particleSystem.addBehavior(new RepelParticles(20, 0.4f), ParticleTag.BEE_FLOCK, ParticleTag.HIVE);

    particleSystem.addBehavior(new RepelParticles(5, 1f), ParticleTag.HIVE);
    particleSystem.addBehavior(new RepelParticles(20, 1f), ParticleTag.HIVE, ParticleTag.HIVE);
    particleSystem.addBehavior(new RepelParticles(20, 3f), ParticleTag.HIVE, ParticleTag.FLYTRAP);
    particleSystem.addBehavior(new RepelParticles(15, 1f), ParticleTag.HIVE, ParticleTag.PLANT);
    particleSystem.addBehavior(new RepelParticles(20, 3f), ParticleTag.HIVE, ParticleTag.SEED);

    particleSystem.addBehavior(new RepelParticles(20, 1f), ParticleTag.BEE_FLOCK, ParticleTag.HIVE);

    particleSystem.addBehavior(new RepelParticles(5, 9f), ParticleTag.HIVE_FOOD, ParticleTag.HIVE);
    particleSystem.addBehavior(new RepelParticles(10, 2), ParticleTag.HIVE, ParticleTag.HIVE_FOOD);
    particleSystem.addBehavior(new SymmetricRepel(20, 0.3f), ParticleTag.HIVE_FOOD);

    particleSystem.addBehavior(new ParticleFriction(0.6f), ParticleTag.HIVE);
    particleSystem.addBehavior(new ParticleFriction(0.8f), ParticleTag.BABY);
    particleSystem.addBehavior(new SymmetricRepel(7, 0.5f), ParticleTag.BABY);
    particleSystem.addBehavior(new RepelParticles(7, 5f), ParticleTag.BABY, ParticleTag.HIVE);

//    for (int i = 0; i < 500; i++) {
//      createBee(particleSystem.getBounds().getRandomPointInside(2), hives.get(0));
//    }
  }


  public void createBee(Vector position, Hive hive) {
    beeCreationQueue.add(new BeeEntity(particleSystem,
        position.x,
        position.y,
        hive,
        this
    ));
  }

  public void createBee(Vector position, int team, BeeColor color, int segments) {
    beeCreationQueue.add(new BeeEntity(particleSystem,
        position.x,
        position.y,
        this,
        team,
        color,
        segments
    ));
  }

  public void addBee(BeeEntity bee) {
    beeCreationQueue.add(bee);
  }

  public void removeBee(BeeEntity bee) {
    beeDeletionQueue.add(bee);
  }

  public void removeHive(Hive hive) {
    hiveDeletionQueue.add(hive);
  }

  public int beeCount() {
    return beeEntities.size();
  }

  @Override
  public void update() {
    beeDeletionQueue.forEach(BeeEntity::destroyParticles);
    beeEntities.removeAll(beeDeletionQueue);
    beeDeletionQueue.clear();
    beeEntities.addAll(beeCreationQueue);
    beeCreationQueue.clear();

    beeEntities.forEach(b -> b.update(lightLevel));


    synchronized (hives) {
      hives.forEach(h -> h.update(lightLevel));
    }

    synchronized (hives) {
      hives.removeAll(hiveDeletionQueue);
      hiveDeletionQueue.clear();
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    beeEntities.forEach(b -> b.draw(graphics));

    synchronized (hives) {
      hives.forEach(h -> h.draw(graphics));
    }
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }

  public List<Hive> getHives() {
    return hives;
  }

  public float getLightLevel() {
    return lightLevel;
  }

  public void createHive(Vector safePoint) {
    synchronized (hives) {
      hives.add(new Hive(
          hives.size(),
          colors[hives.size() % colors.length],
          safePoint,
          particleSystem,
          plantSystem,
          this));
    }
  }
}
