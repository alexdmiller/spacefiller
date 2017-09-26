package particles.behaviors;

import boids.Boid;
import boids.behaviors.Behavior;
import particles.Particle;
import processing.core.PVector;
import spacefiller.remote.Mod;

import java.util.List;

public class WiggleParticles extends ParticleBehavior {
  @Mod(min = 0, max = 10, defaultValue = 1)
  public float wiggleAmplitudeMultiplier = 1;

  @Mod(min = 0, max = 10, defaultValue = 1)
  public float wiggleStepMultiplier = 10;

  @Override
  public void apply(List<Particle> particles) {
    for (Particle particle : particles) {
      PVector wiggleDirection = particle.position.copy().rotate((float) Math.PI / 2f);

      if (!particle.hasUserData("wiggleStep")) {
        particle.setUserData("wiggleStep", 0f);
      }
      float wiggleStep = (float) particle.getUserData("wiggleStep");

      wiggleDirection.setMag((float) Math.sin(wiggleStep) * particle.velocity.mag() * wiggleAmplitudeMultiplier);
      particle.position.add(wiggleDirection);
      wiggleStep += particle.position.mag() * wiggleStepMultiplier;
      particle.setUserData("wiggleStep", wiggleStep);
    }
  }
}
