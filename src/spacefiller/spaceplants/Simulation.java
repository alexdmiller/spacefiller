package spacefiller.spaceplants;

import processing.core.PApplet;
import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.plants.PlantSystem;
import spacefiller.particles.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
  private static final int INTERACTION_THRESHOLD = 100;
  private static final int STARTUP_THRESH = 5;
  private static final int MAX_MODE_LIFE = 200;
  private final CircleBounds plantBounds;
  private final CircleBounds hiveBounds;
  private final CircleBounds beeBounds;
  private final CircleBounds dustBounds;
  private final SoftBounds softBounds;
  private final SymmetricRepel symmetricRepel;
  private int frame;

  private List<System> systems;
  private ParticleSystem particleSystem;
  private PlantSystem plantSystem;
  private BeeSystem beeSystem;
  private DustSystem dustSystem;
  private float lightLevel;

  private FlowBehavior flowBehavior;
  private float width;
  private float height;

  private int framesSinceLastInteraction = 0;
  private int framesSinceLastModeSwitch = 0;
  private boolean interactionInProgress = false;

  public CircleBounds getDustBounds() {
    return dustBounds;
  }

  public PlantSystem getPlantSystem() {
    return plantSystem;
  }

  public DustSystem getDustSystem() {
    return dustSystem;
  }

  public enum Mode {
    PLANT, WORM
  }

  private int currentModeIndex = 0;

  public Simulation(
      float width,
      float height,
      PApplet parent) {

    this.width = width;
    this.height = height;
    this.systems = new ArrayList<>();
    this.particleSystem = new ParticleSystem(width, height, 20);

    this.plantSystem = new PlantSystem(particleSystem);
    this.beeSystem = new BeeSystem(particleSystem, plantSystem);

    this.dustSystem = new DustSystem(particleSystem, width, height);

    systems.add(dustSystem);
    systems.add(plantSystem);
    systems.add(beeSystem);

//    systems.add(flytrapSystem);
//    systems.add(trashSystem);

    plantBounds = new CircleBounds(100, 1, 1);
    particleSystem.addBehavior(plantBounds, ParticleTag.PLANT);

    hiveBounds = new CircleBounds(30, 1, 1);
    particleSystem.addBehavior(hiveBounds, ParticleTag.HIVE);

    beeBounds = new CircleBounds(130, 1, 1);
    particleSystem.addBehavior(beeBounds, ParticleTag.BEE);

    dustBounds = new CircleBounds(140, 1, 1);
    particleSystem.addBehavior(dustBounds, ParticleTag.DUST);

    softBounds = new SoftBounds();
    particleSystem.addBehavior(softBounds);

    //    CircleBounds starBounds = new CircleBounds(150, 1, 1);
//    particleSystem.addBehavior(starBounds, ParticleTag.DUST);

//    RepelFixedPoints starRepel = new RepelFixedPoints(100, 20);
//    starRepel.addFixedPoint(particleSystem.getBounds().getCenter());
//    particleSystem.addBehavior(starRepel, ParticleTag.DUST);

    RepelParticles repelDust = new RepelParticles(15, 0.5f);
    particleSystem.addBehavior(repelDust, ParticleTag.DUST);

//    RepelFixedPoints starAttract = new RepelFixedPoints(400, -19);
//    starAttract.addFixedPoint(particleSystem.getBounds().getCenter());
//    particleSystem.addBehavior(starAttract, ParticleTag.DUST);


    //particleSystem.addBehavior(new ReflectiveBounds());
    symmetricRepel = new SymmetricRepel(5, 1);
    particleSystem.addBehavior(
        symmetricRepel,
        ParticleTag.GLOBAL_REPEL);
//    particleSystem.printBehaviors();

    plantSystem.createSeed(new Vector(width / 2, height / 2));
  }

  public void changeMode(int index) {
     currentModeIndex = index;
  }

  public void update() {
    frame++;
    framesSinceLastModeSwitch++;

    if (framesSinceLastInteraction > 2) {
      interactionInProgress = false;
    }

    //java.lang.System.out.println(framesSinceLastModeSwitch);
    if (framesSinceLastModeSwitch > MAX_MODE_LIFE) {
      changeMode((currentModeIndex + 1) % Mode.values().length);
      framesSinceLastModeSwitch = 0;
      framesSinceLastInteraction = 0;
    }

    framesSinceLastInteraction++;

    if (Rnd.random.nextDouble() < 0) {
      Vector safePoint = findSafePoint(1);
      if (safePoint != null) {
        if (Rnd.random.nextDouble() < 0.5) {
          plantSystem.createSeed(safePoint);
        } else {
          beeSystem.createHive(safePoint);
        }
      }
    }

    systems.forEach(System::update);
    particleSystem.update();
  }

  public void clear() {
    for (int i = 0; i < particleSystem.getParticles().size(); i++) {
      particleSystem.getParticles().get(i).setUserData("kill", true);
    }

    for (int i = 0; i < 3; i++) {
      beeSystem.createHive(new Vector(width / 2 + Rnd.random.nextDouble() * 100 - 50, height / 2+ Rnd.random.nextDouble() * 100 - 50));
    }
    for (int i = 0; i < 5; i++) {
      plantSystem.createSeed(new Vector(width / 2 + Rnd.random.nextDouble() * 100 - 50, height / 2+ Rnd.random.nextDouble() * 100 - 50));
    }
  }

  public Vector findSafePoint(int maxTries) {
    Vector position = new Vector(
        (float) (Rnd.random.nextDouble() * (particleSystem.getBounds().getWidth() - 100) + 50),
        (float) (Rnd.random.nextDouble() * (particleSystem.getBounds().getHeight() - 100) + 50));
    float density = particleSystem.getDensity(position);

    for (int i = 0; i < maxTries; i++) {
      if (density < Params.f(PName.MAX_PLANT_DENSITY) / 2) {
        return position;
      }
      position = new Vector(
          (float) Rnd.random.nextDouble() * particleSystem.getBounds().getWidth(),
          (float) Rnd.random.nextDouble() * particleSystem.getBounds().getHeight());
      density = particleSystem.getDensity(position);
    }

    return null;
  }


  public void draw(PGraphics graphics) {
    systems.forEach(s -> s.draw(graphics));
  }

  public void preDraw(PGraphics graphics) {
//    fluidSystem.draw(graphics);
  }

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }

  public BeeSystem getBeeSystem() {
    return beeSystem;
  }

  public int getWidth() {
    return (int) width;
  }

  public int getHeight() {
    return (int) height;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }

  public FlowBehavior getFlowBehavior() {
    return flowBehavior;
  }

  public float getLightLevel() {
    return lightLevel;
  }

  public CircleBounds getPlantBounds() {
    return plantBounds;
  }

  public CircleBounds getHiveBounds() {
    return hiveBounds;
  }

  public CircleBounds getBeeBounds() {
    return beeBounds;
  }

  public SymmetricRepel getSymmetricRepel() {
    return symmetricRepel;
  }
}
