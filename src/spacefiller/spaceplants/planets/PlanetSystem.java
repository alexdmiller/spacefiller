package spacefiller.spaceplants.planets;

import processing.core.PGraphics;
import spacefiller.math.Vector;
import spacefiller.math.sdf.*;
import spacefiller.particles.Bounds;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.SPSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlanetSystem implements SPSystem {
  private List<Planet> planetList;
  private Wrapper finalSdfOutput;
  private ParticleSystem particleSystem;
  private ParticleSystem planetParticleSystem;
  private float noiseAmplitude;
  private float noiseScale;
  private float sdfSmooth;

  public PlanetSystem(
      ParticleSystem particleSystem,
      float repelThreshold,
      float attractThreshold,
      float noiseAmplitude,
      float noiseScale,
      float sdfSmooth) {
    planetList = new ArrayList<>();
    planetParticleSystem = new ParticleSystem(particleSystem.getBounds(), 200, 200);
    this.particleSystem = particleSystem;
    this.noiseAmplitude = noiseAmplitude;
    this.noiseScale = noiseScale;
    this.sdfSmooth = sdfSmooth;

//    planetSystem = new ParticleSystem(new Bounds(canvas.width, canvas.height), config.maxParticles, 200);
//    planetSystem.setDebugDraw(true);
//    for (int i = 0; i < 10; i++) {
//      Particle p = planetSystem.createParticle(planetSystem.getBounds().getRandomPointInside(2));
//      p.setRandomVelocity(1, 2, 2);
//      p.setRadius((float) (Math.random() * 200));
//    }
//
    planetParticleSystem.addBehavior(new SoftBounds(10, 5, 3));
    planetParticleSystem.addBehavior(new ParticleFriction(0.95f));
    planetParticleSystem.addBehavior(new RepelParticles(repelThreshold, 2f, true));
    planetParticleSystem.addBehavior(new AttractParticles(attractThreshold, 0.0001f));

    finalSdfOutput = new Wrapper();

    particleSystem.addBehavior(new FollowGradient(finalSdfOutput, 1f, true), ParticleTag.DUST);
    particleSystem.addBehavior(new FollowGradient(finalSdfOutput, 0.5f, true), ParticleTag.HIVE);
//    particleSystem.addBehavior(new FollowGradient(finalSdfOutput, 0.5f, true), ParticleTag.PLANT);
  }

  public void createPlanet(float radius) {
    Vector position = planetParticleSystem.getBounds().getRandomPointInside(2);
    Planet p = new Planet(position, radius, planetParticleSystem);
    planetList.add(p);
    recomputeSdf();
  }

  private void recomputeSdf() {
    List<FloatField2> circles = planetList.stream().map(planet -> planet.sdf).collect(Collectors.toList());
    FloatField2 sdf = new Union(circles, sdfSmooth);
    sdf = new NoiseDistort(sdf, noiseAmplitude, noiseScale);
    sdf = new Floor(sdf);
//    sdf = new Normalize(planetParticleSystem.getBounds(), 10, sdf);
    finalSdfOutput.setField(sdf);
  }

  @Override
  public void update() {
    for (Planet p : planetList) {
      p.update();
    }
    planetParticleSystem.update();
  }

  @Override
  public void draw(PGraphics graphics) {
//    FieldVisualizer.drawField(finalSdfOutput, graphics, 10, 0, 1);
//    planetParticleSystem.setDebugDraw(true);
    planetParticleSystem.draw(graphics);
  }
}
