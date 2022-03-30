package spacefiller.particles.behaviors;

import spacefiller.math.Vector;
import spacefiller.particles.Particle;

public class DonutBounds extends LocalBehavior {
  @Override
  public void apply(Particle p) {
    Vector a = getParticleSystem().getBounds().getTopBackLeft();
    Vector b = getParticleSystem().getBounds().getBottomFrontRight();

    p.setTeleportFlag(false);
    if (p.getPosition().x < a.x) {
      p.getPosition().x = b.x;
      p.setTeleportFlag(true);
    } else if (p.getPosition().x > b.x) {
      p.getPosition().x = a.x;
      p.setTeleportFlag(true);
    }

    if (p.getPosition().y < a.y) {
      p.getPosition().y = b.y;
      p.setTeleportFlag(true);
    } else if (p.getPosition().y > b.y) {
      p.getPosition().y = a.y;
      p.setTeleportFlag(true);
    }

    if (p.getPosition().z < a.z) {
      p.getPosition().z = b.z;
      p.setTeleportFlag(true);
    } else if (p.getPosition().z > b.z) {
      p.getPosition().z = a.z;
      p.setTeleportFlag(true);
    }
  }
}
