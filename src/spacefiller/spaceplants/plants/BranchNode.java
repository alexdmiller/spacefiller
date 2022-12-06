package spacefiller.spaceplants.plants;

import processing.core.PGraphics;
import spacefiller.math.Rnd;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;

public class BranchNode extends PlantNode {
  public BranchNode(Particle node, PlantDNA dna, PlantSystem plantSystem, int depthCounter) {
    super(node, dna, plantSystem, depthCounter);
    growthCounter = (int) Math.round((dna.getBranchingFactor() + (Rnd.random.nextDouble() * 0.5) * dna.getBranchingFactorDeviation()) + dna.getBranchingFactorFalloff() * depthCounter);
  }

  public void grow() {
    if (growthCounter > 0 && Rnd.random.nextDouble() < Params.f(PName.GROWTH_PROBABILITY)) {
      growthCounter--;
      float angle = (float) (Rnd.random.nextDouble() * Math.PI * 6f);

      Vector offset = new Vector(
          (float) Math.cos(angle) * dna.getConnectionLength(),
          (float) (Math.sin(angle) * dna.getConnectionLength()));

      ParticleSystem particleSystem = plantSystem.getParticleSystem();
      Particle newParticle = particleSystem.createParticle(Vector.add(particle.getPosition(), offset));
      newParticle.setTeam(particle.getTeam());

      attachNode(new PlantNode(newParticle, dna, plantSystem, depthCounter + 1));
    }
  }

  @Override
  public void draw(PGraphics graphics, PlantColor color) {
    drawSprings(graphics, color.getBranchColor(dna, particle.getLife()));
  }
}