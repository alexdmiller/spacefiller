package lusio.generators;

import common.Particle;
import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/12/17.
 */
public class MeshGenerator extends SceneGenerator {
  private int numParticles;
  private List<Particle> particles;
  private float maxForce;
  private float angle;

  public MeshGenerator(int numParticles, float maxForce) {
    particles = new ArrayList<>();
    this.maxForce = maxForce;
    this.numParticles = numParticles;
  }

  @Override
  public void setup() {
    for (int i = 0; i < numParticles; i++) {
      Particle p = new Particle(getBounds().getRandomPointInside());
      p.setRandomVelocity(1, 2);
      particles.add(p);
    }
  }

  @Override
  public void draw(PGraphics graphics) {
    angle += 0.01;

    graphics.strokeWeight(1);
    graphics.rotateX(0.5f);
    graphics.rotateY((float) Math.PI / 5 + angle);

    graphics.box(getBounds().getWidth(), getBounds().getHeight(), getBounds().getDepth());

    graphics.strokeWeight(4);
    for (Particle p : particles) {
      getBounds().constrain(p);
      p.flushForces(1);
      p.update();
      graphics.point(p.position.x, p.position.y, p.position.z);
    }
  }
}
