//package spacefiller.livingwall.planets;
//
//import processing.core.PGraphics;
//import spacefiller.livingwall.PName;
//import spacefiller.livingwall.Params;
//import spacefiller.livingwall.System;
//import spacefiller.livingwall.math.Vector;
//import spacefiller.livingwall.particles.Particle;
//import spacefiller.livingwall.particles.ParticleSystem;
//import spacefiller.livingwall.particles.ParticleTag;
//import spacefiller.livingwall.particles.behaviors.LocalBehavior;
//import spacefiller.livingwall.plants.PlantSystem;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlanetSystem implements System {
//  private List<Planet> planets;
//  private PlantSystem plantSystem;
//  private ParticleSystem particleSystem;
//
//  public PlanetSystem(PlantSystem plantSystem) {
//    planets = new ArrayList<>();
//    this.plantSystem = plantSystem;
//    this.particleSystem = plantSystem.getParticleSystem();
//
//    particleSystem.addBehavior(new LocalBehavior() {
//      @Override public void apply(Particle particle) {
//        planets.forEach(planet -> {
//          Vector vector = planet.getPosition();
//          Vector delta = Vector.sub(particle.getPosition(), vector);
//          if (delta.magnitude() < planet.getRadius() + 3) {
//            delta.normalize();
//            particle.applyForce(delta);
//          }
//        });
//      }
//    }, ParticleTag.PLANT);
//  }
//
//  public void createPlanet(float x, float y) {
//    this.planets.add(new Planet(new Vector(x, y), plantSystem,
//        plantSystem.getParticleSystem()));
//  }
//
//
//
//  @Override public void update() {
//
//  }
//
//  @Override public void draw(PGraphics graphics) {
//    planets.forEach(p -> {
//      p.draw(graphics);
//    });
//  }
//}
