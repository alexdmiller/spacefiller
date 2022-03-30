package spacefiller.spaceplants;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;
import spacefiller.particles.behaviors.LocalBehavior;

public class BlackHole extends LocalBehavior {
  private Vector position;
  private float strength;
  private boolean killParticles;

  public BlackHole() {
    position = new Vector();
  }

  public Vector getPosition() {
    return position;
  }

  public void setPosition(Vector position) {
    this.position = position;
  }

  public float getStrength() {
    return strength;
  }

  public void setStrength(float strength) {
    this.strength = strength;
  }

  @Override public void apply(Particle particle) {
    Vector delta = position.copy().sub(particle.getPosition());

    float mag = delta.magnitude();
    if (killParticles && mag < 20) {
      particle.setUserData("kill", true);
    }

    delta.normalize();
    float force = strength / mag;
    particle.applyForce(delta.copy().mult(force));
  }

  public void setKillParticles(boolean b) {
    killParticles = b;
  }
}
