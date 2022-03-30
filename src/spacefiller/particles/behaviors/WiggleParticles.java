package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class WiggleParticles extends LocalBehavior {
  public float wiggleAmplitudeMultiplier = 1;
  public float wiggleStepMultiplier = 10;

  @Override
  public void apply(Particle particle) {
    Vector wiggleDirection = particle.getPosition().copy().rotate((float) Math.PI / 2f);

    if (!particle.hasUserData("wiggleStep")) {
      particle.setUserData("wiggleStep", 0f);
    }
    float wiggleStep = (float) particle.getUserData("wiggleStep");

    wiggleDirection.setMag((float) Math.sin(wiggleStep) * particle.getVelocity().magnitude() * wiggleAmplitudeMultiplier);
    particle.getPosition().add(wiggleDirection);
    wiggleStep += particle.getPosition().magnitude() * wiggleStepMultiplier;
    particle.setUserData("wiggleStep", wiggleStep);
  }
}
