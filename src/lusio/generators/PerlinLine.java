package lusio.generators;

import lusio.Lusio;
import particles.Bounds;
import particles.Particle;
import particles.ParticleSystem;
import particles.behaviors.ParticleBehavior;
import particles.renderers.ParticleRenderer;
import processing.core.PGraphics;
import processing.core.PVector;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class PerlinLine extends SceneGenerator {
  private float t;
  private float updateSpeed = 0.01f;

  // TODO: why does this need to have a max force?
  public PerlinLine() {
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.strokeWeight(1);
    t += updateSpeed;

    graphics.rotateY(t);
    PVector point = new PVector();
    PVector oldPoint = new PVector();
    for (int i = 0; i < 40; i++) {
      oldPoint.set(point);

      point.x = Lusio.instance.noise((float) point.y + i / 10, 1, t) * 200 - 100;
      point.y += 10;
      point.z = Lusio.instance.noise((float) i / 10, 2, t) * 200 - 100;

      graphics.line(oldPoint.x, oldPoint.y, oldPoint.z, point.x, point.y, point.z);
    }
  }

}
