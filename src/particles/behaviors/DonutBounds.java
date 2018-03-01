package particles.behaviors;

import particles.Particle;

import java.util.Iterator;
import java.util.List;

public class DonutBounds extends ParticleBehavior {
  @Override
  public void apply(List<Particle> particles) {
    float width = getParticleSystem().getBounds().getWidth();
    float height = getParticleSystem().getBounds().getHeight();
    float depth = getParticleSystem().getBounds().getDepth();

    for (Particle p : particles) {
      p.setTeleportFlag(false);
      if (p.position.x < -width / 2) {
        p.position.x = width / 2;
        p.setTeleportFlag(true);
      } else if (p.position.x > width / 2) {
        p.position.x = -width / 2;
        p.setTeleportFlag(true);
      }

      if (p.position.y < -height / 2) {
        p.position.y = height / 2;
        p.setTeleportFlag(true);
      } else if (p.position.y > height / 2) {
        p.position.y = -height / 2;
        p.setTeleportFlag(true);
      }

      if (p.position.z < -depth / 2) {
        p.position.z = depth / 2;
        p.setTeleportFlag(true);
      } else if (p.position.z > depth / 2) {
        p.position.z = -depth / 2;
        p.setTeleportFlag(true);
      }
    }
  }
}
