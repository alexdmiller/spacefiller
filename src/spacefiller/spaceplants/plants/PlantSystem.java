package spacefiller.spaceplants.plants;

import processing.core.PGraphics;
import spacefiller.math.Vector;
import spacefiller.spaceplants.SPSystem;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.Spring;
import spacefiller.particles.behaviors.ParticleFriction;
import spacefiller.particles.behaviors.RepelParticles;
import spacefiller.particles.behaviors.SymmetricRepel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlantSystem implements SPSystem {
  // the maximum nodes allowed in the system; otherwise it gets too slow.
  private static final int MAX_PLANT_NODES = 10000;
  private final SymmetricRepel symmetricRepel;

  // underlying particle system for basic particle physics
  private ParticleSystem particleSystem;

  private List<PlantNode> plantNodes;
  private List<PlantNode> creationQueue;
  private List<PlantNode> deleteQueue;

  private PlantColor plantColor;

  private float lightLevel = 1;

  public SymmetricRepel getSymmetricRepel() {
    return symmetricRepel;
  }

  public PlantSystem(ParticleSystem particleSystem) {
    this.particleSystem = particleSystem;

    symmetricRepel = new SymmetricRepel(15, 1f);
    particleSystem.addBehavior(symmetricRepel, ParticleTag.PLANT);
    particleSystem.addBehavior(new ParticleFriction(0.5f), ParticleTag.PLANT);
    particleSystem.addBehavior(new RepelParticles(15, 1f), ParticleTag.FLOWER);
    particleSystem.addBehavior(new RepelParticles(20, 2), ParticleTag.EXCITED, ParticleTag.PLANT);
    particleSystem.addBehavior(new RepelParticles(20, 1), ParticleTag.SEED);
    particleSystem.addBehavior(new RepelParticles(20, 2f), ParticleTag.PLANT, ParticleTag.HIVE);
    particleSystem.addBehavior(new RepelParticles(20, 0.5f), ParticleTag.PLANT, ParticleTag.FLYTRAP);

    particleSystem.addBehavior(new ParticleFriction(0.95f), ParticleTag.SEED);

    plantNodes = Collections.synchronizedList(new ArrayList<>());
    creationQueue = new ArrayList<>();
    deleteQueue = new ArrayList<>();

    plantColor = new PlantColor();
  }

  @Override
  public void update() {
    for (PlantNode node : plantNodes) {
      node.update();

      // grow the plants (if they can be grown)
      if (!node.condemned && plantNodes.size() < MAX_PLANT_NODES) {
        node.grow();
      }

      // particles move towards the average position of the particles they're
      // connected to. this flattens out chains of particles.
      if (node.particle.getConnections().size() > 0) {
        Vector avg = new Vector();
        for (Spring spring : node.particle.getConnections()) {
          Particle other = spring.other(node.particle);
          avg.add(other.getPosition());
        }
        avg.div(node.particle.getConnections().size());

        Vector delta = Vector.sub(avg, node.particle.getPosition());
        delta.mult(0.1f);
        node.particle.applyForce(delta);
      }
    }

    // add nodes from creation queue; remove nodes from deletion queue.
    plantNodes.addAll(creationQueue);
    plantNodes.removeAll(deleteQueue);

    deleteQueue.clear();
    creationQueue.clear();
  }

  @Override
  public void draw(PGraphics graphics) {
    plantNodes.forEach(node -> node.draw(graphics, plantColor));
  }

  public ParticleSystem getParticleSystem() {
    return particleSystem;
  }

  public void addPlantNode(PlantNode trunkNode) {
    creationQueue.add(trunkNode);
  }

  public void removePlantNode(PlantNode node) {
    deleteQueue.add(node);
  }

  public SeedNode createSeed(Vector position) {
    Particle particle = particleSystem.createParticle(position);
    SeedNode node = new SeedNode(particle, PlantDNA.createNewDNA(), this, 0);
    addPlantNode(node);
    return node;
  }

  public SeedNode createSeed(Vector position, PlantDNA dna) {
    Particle particle = particleSystem.createParticle(position);
    SeedNode node = new SeedNode(particle, dna, this, 0);
    addPlantNode(node);
    return node;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
    plantColor.setLightLevel(lightLevel);
  }

  public float getLightLevel() {
    return lightLevel;
  }
}
