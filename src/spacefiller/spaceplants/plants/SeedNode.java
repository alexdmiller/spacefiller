package spacefiller.spaceplants.plants;

import processing.core.PApplet;
import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.Utils;
import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.Spring;

import static spacefiller.math.PerlinNoise.noise;
import static spacefiller.particles.ParticleTag.SEED;

public class SeedNode extends PlantNode {
  private boolean planted = false;

  public SeedNode(Particle particle, PlantDNA dna, PlantSystem plantSystem, int depthCounter) {
    super(particle, dna, plantSystem, depthCounter);

    particle.removeTag(ParticleTag.PLANT);
    particle.addTag(SEED);
    particle.setMass(1f);

    growthCounter = (int) Math.round(dna.getSeedBranchingFactor() + (Rnd.random.nextDouble() * 0.5) * dna.getBranchingFactorDeviation());
  }

  public void grow() {
    if (planted) {
      super.grow();
    }
  }

  public void update() {
    super.update();

    if (!planted) {
      if (particle.getLife() > Params.i(PName.SEED_LIFESPAN)) {
        destroy();
      }

      float angle = (float) (noise(
                particle.getPosition().x/40f,
                particle.getPosition().y/40f,
                dna.getPlantEntropy() * 100) * 12 * Math.PI);
      float mag = 0.1f * noise(
                particle.getPosition().x,
                particle.getPosition().y,
                100 + dna.getPlantEntropy() * 100);

      particle.applyForce(Vector.fromAngle(angle).mult(mag));
    }

    int numPlantNodes = plantSystem.getParticleSystem().getParticlesWithTag(ParticleTag.PLANT).size();

    if (!planted
        && numPlantNodes < Params.i(PName.MAX_PLANT_PARTICLES)
        && (particle.getLife() > dna.getTimeToPlant() || numPlantNodes < Params.i(PName.MIN_PLANT_PARTICLES))) {
      Particle hive = particle.findClosest(ParticleTag.HIVE);
      Particle seed = particle.findClosest(SEED);

      float hiveDistance = hive == null ? 9999 : hive.getPosition().dist(particle.getPosition());
      float seedDistance = seed == null ? 9999 : seed.getPosition().dist(particle.getPosition());

      if (hiveDistance > Params.f(PName.SEED_DESIRED_HIVE_DISTANCE) &&
          seedDistance > Params.f(PName.SEED_DESIRED_SEED_DISTANCE)) {
        planted = true;
        //particle.setStatic(true);
        particle.removeTag(SEED);
        particle.addTag(ParticleTag.PLANT);
      }
    }
  }

  @Override
  public void draw(PGraphics graphics, PlantColor color) {
    drawSprings(graphics, color.getBranchColor(dna, particle.getLife()));

    graphics.stroke(color.getSeedColor(dna, particle.getLife()));
    graphics.strokeWeight(dna.getSeedSize());
    graphics.point(particle.getPosition().x, particle.getPosition().y);
  }
}