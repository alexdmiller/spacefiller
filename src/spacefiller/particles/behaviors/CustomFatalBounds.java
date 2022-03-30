package spacefiller.particles.behaviors;

import spacefiller.particles.Bounds;
import spacefiller.particles.Particle;

public class CustomFatalBounds extends LocalBehavior {
  private Bounds bounds;

  public CustomFatalBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  @Override
  public void apply(Particle particle) {
    // TODO: implement removeFlag logic in particle system
    if (!bounds.contains(particle.getPosition())) {
      particle.setRemoveFlag(true);
    }
  }
}
