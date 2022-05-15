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
import spacefiller.particles.ParticleTag;

import static processing.core.PConstants.RGB;

public class FlowerNode extends PlantNode {
  private int detachedTimer;

  public FlowerNode(Particle particle, PlantDNA dna, PlantSystem plantSystem, int depthCounter) {
    super(particle, dna, plantSystem, depthCounter);
    particle.setUserData("plant_node", this);
  }

  public void grow() { }

//  @Override
//  public void condemn() {
//    detach();
//    particle.addTag(ParticleTag.DEAD_FLOWER);
//  }

  public void update() {
    super.update();

    if (detached) {
      detachedTimer++;
    }

    if (particle.hasTag(ParticleTag.DEAD_FLOWER) && detachedTimer > Params.i(PName.FLOWER_DETACHED_LIFESPAN)) {
      // randomize a little bit so all flowers don't die at the exact same moment
      if (Rnd.random.nextDouble() < 1f) {
        destroy();

        int numPlants = plantSystem.getParticleSystem().getParticlesWithTag(ParticleTag.PLANT).size();
        if (numPlants < Params.i(PName.MAX_PLANT_PARTICLES) && Rnd.random.nextDouble() < Params.f(PName.FLOWER_TO_SEED_CHANCE)) {
          plantSystem.createSeed(particle.getPosition().copy().add(Vector.random2D()));
        }
      }
    } else {
      if (particle.getLife() == Params.i(PName.BUD_TO_FLOWER_TIME)) {
        particle.addTag(ParticleTag.FLOWER);
      }

      if (excitement > Params.f(PName.EXCITEMENT_THRESHOLD)) {
        particle.addTag(ParticleTag.EXCITED);
      } else {
        particle.removeTag(ParticleTag.EXCITED);
      }
    }
  }

  @Override
  public void draw(PGraphics graphics, PlantColor color) {
    float b = (float) ((Math.sin(
        Utils.getMillis() / (float) Params.i(PName.PLANT_LIGHT_PERIOD)
            - depthCounter/ Params.f(PName.PLANT_LIGHT_SPACING)
            + dna.getPlantEntropy() * 100)) * (1 - plantSystem.getLightLevel()));

    if (particle.getLife() < Params.i(PName.BUD_TO_FLOWER_TIME)) {
      graphics.stroke(PApplet.lerpColor(
          color.getFlowerBudColor(dna, particle.getLife()),
          color.getFlowerBudLightColor(dna, particle.getLife()),
          b, RGB));
      graphics.strokeWeight((float) particle.getLife() / Params.i(PName.BUD_TO_FLOWER_TIME) * dna.getBudMaxSize());
    } else {
      graphics.stroke(PApplet.lerpColor(
          color.getFlowerColor(dna, particle.getLife(), excitement),
          color.getFlowerLightColor(dna, particle.getLife()),
          b, RGB));
      graphics.strokeWeight(dna.getFlowerSize() + excitement * dna.getExcitementGrowth());
    }

    graphics.point(particle.getPosition().x, particle.getPosition().y);
  }
}
