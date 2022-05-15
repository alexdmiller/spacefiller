package spacefiller.spaceplants.plants;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.Utils;
import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.Spring;
import spacefiller.particles.behaviors.FlowBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlantNode {
  public static final float SPRING_STRENGTH = 0.01f;

  protected Particle particle;

  // a plant node owns the springs that it grows
  protected List<Spring> springs;
  protected List<PlantNode> downstream;

  // how many growths can come out of this node
  protected int growthCounter = 1;

  protected PlantSystem plantSystem;

  // counter that keeps track of how far this node is from the seed
  protected int depthCounter;

  protected float excitement;
  protected PlantDNA dna;

  // a condemned node will die soon
  protected boolean condemned = false;

  // a detached node is an orphan and doesn't belong to any other node
  protected boolean detached = false;

  public PlantNode(Particle particle, PlantDNA dna, PlantSystem plantSystem, int depthCounter) {
    particle.addTag(ParticleTag.PLANT);

    this.particle = particle;
    this.particle.setMass(dna.getParticleMass());
    this.plantSystem = plantSystem;
    this.dna = dna;
    this.depthCounter = depthCounter;
    this.springs = new ArrayList<>();
    this.downstream = new ArrayList<>();
  }

  public void detach() {
    particle.detachSprings();

    // setting detached on this node will notify the parent node
    // that the parent needs to remove this node from its downstream
    // list.
    detached = true;
  }

  protected void drawSprings(PGraphics graphics, int color) {
    synchronized (springs) {
      for (Spring spring : springs) {
        graphics.strokeWeight(Params.getStrokeWeight(plantSystem.getLightLevel()));

        float b = (float) ((Math.sin(
            Utils.getMillis() / (float) Params.i(PName.PLANT_LIGHT_PERIOD)
                - depthCounter/ Params.f(PName.PLANT_LIGHT_SPACING)
                + dna.getPlantEntropy() * 100)) * (1 - plantSystem.getLightLevel()));
        graphics.stroke(PApplet.lerpColor(color, 0xff999999, b, PConstants.RGB));
        Vector pos1 = spring.getN1().getPosition();
        Vector pos2 = spring.getN2().getPosition();
        graphics.line(pos1.x, pos1.y, pos2.x, pos2.y);
      }
    }
  }

  public void update() {
    excitement *= Params.f(PName.EXCITEMENT_FALLOFF);
    if (particle.hasUserData(FlowBehavior.FIELD_FORCE_KEY)) {
      Vector f = (Vector) particle.getUserData(FlowBehavior.FIELD_FORCE_KEY);
      excitement = Math.min(1, excitement + f.magnitude());
    }

    // remove any nodes that have marked themselves as detached from our downstream list
    for (int i = downstream.size() - 1; i >= 0; i--) {
      if (downstream.get(i).detached) {
        downstream.remove(i);
      }
    }

    synchronized (springs) {
      springs = springs.stream().filter(spring -> !spring.removeFlag).collect(Collectors.toList());
    }

    for (Spring spring : springs) {
      spring.setSpringLength(dna.getConnectionLength() + excitement * 3);
    }

    // if the plant is too old, then kill it (but not if it's already a dead flower)
    boolean plantTooOld = particle.getLife() > dna.getMaxAge() && !particle.hasTag(ParticleTag.HIVE_FOOD);

    // if the plant node has been condemned, then add a small amount of randomness to let it maybe live a little longer
    boolean plantCondemned = (condemned && Rnd.random.nextDouble() < Params.f(PName.CONDEMNED_DEATH_CHANCE));

    boolean markedForKill = particle.hasUserData("kill") && (boolean) particle.getUserData("kill");

    if (plantTooOld || plantCondemned || (markedForKill)) {
      die();
    }
  }

  public void draw(PGraphics graphics, PlantColor color) {
    drawSprings(graphics, color.getBranchColor(dna, particle.getLife()));
  }

  public void grow() {
    doGrow();
  }

  // todo: add comments to these similarly named methods
  public void die() {
    for (PlantNode node : downstream) {
      if (!node.detached) {
        node.condemn();
      }
    }

    detach();
    destroy();
  }

  protected void condemn() {
    for (PlantNode node : downstream) {
      if (!node.detached) {
        node.condemn();
      }
    }

    condemned = true;
  }

  protected void doGrow() {
    int numPlants = plantSystem.getParticleSystem().getParticlesWithTag(ParticleTag.PLANT).size();
    if (!condemned && growthCounter > 0 && Rnd.random.nextDouble() < Params.f(PName.GROWTH_PROBABILITY)
        && numPlants < Params.i(PName.MAX_PLANT_PARTICLES)) {
      float angle = (float) (Rnd.random.nextDouble() * Math.PI * 6f);

      Vector offset = new Vector(
              (float) Math.cos(angle) * dna.getConnectionLength() / 2f,
              (float) (Math.sin(angle)  * dna.getConnectionLength() / 2f));

      ParticleSystem particleSystem = plantSystem.getParticleSystem();

      Vector newPosition = Vector.add(particle.getPosition(), offset);

      if (particleSystem.getDensity(newPosition) < Params.f(PName.MAX_PLANT_DENSITY)) {
        growthCounter--;

        Particle newParticle = particleSystem.createParticle(newPosition);
        newParticle.setTeam(particle.getTeam());

        synchronized (springs) {
          if (depthCounter < (dna.getMaxDepth() + (Rnd.random.nextDouble() - 0.5) * dna.getMaxDepthDeviation())) {
            float r = (float) Rnd.random.nextDouble();

            if (r < dna.getBranchChance()) {
              // create a branch node
              attachNode(new BranchNode(newParticle, dna, plantSystem, depthCounter + 1));
            } else {
              // create a regular-ass node
              attachNode(new PlantNode(newParticle, dna, plantSystem, depthCounter + 1));
            }
          } else {
            attachNode(new FlowerNode(newParticle, dna, plantSystem, depthCounter + 1));
          }
        }
      }
    }
  }

  protected void attachNode(PlantNode node) {
    ParticleSystem particleSystem = plantSystem.getParticleSystem();
    springs.add(particleSystem.createSpring(particle, node.particle, dna.getConnectionLength(), SPRING_STRENGTH));
    downstream.add(node);
    plantSystem.addPlantNode(node);
  }

  public Particle getParticle() {
    return particle;
  }

  public void destroy() {
    particle.setRemoveFlag(true);
    plantSystem.removePlantNode(this);
  }
}