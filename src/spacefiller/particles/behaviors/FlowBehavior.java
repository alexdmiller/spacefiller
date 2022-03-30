package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class FlowBehavior extends LocalBehavior {
  public static final String FIELD_FORCE_KEY = "field_force";

  private FlowField field;
  private float killVelocity;

  public FlowBehavior(FlowField field, float killVelocity) {
    this.field = field;
    this.killVelocity = killVelocity;
  }

  @Override
  public void apply(Particle particle) {
    Vector forceFromField = field.getForceAt(particle.getPosition());
    particle.applyForce(forceFromField);
    particle.setUserData(FIELD_FORCE_KEY, forceFromField);

    if (forceFromField.magnitude() > killVelocity) {
      particle.setUserData("kill", true);
    }
  }

  public interface FlowField {
    Vector getForceAt(Vector p);
  }
}
