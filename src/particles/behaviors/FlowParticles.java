package particles.behaviors;

import common.VectorField;
import spacefiller.remote.Mod;
import particles.Particle;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

import particles.Particle;
import processing.core.PVector;

import java.util.List;

public class FlowParticles extends ParticleBehavior {
  private VectorField field;

  @Mod
  public float weight;

  @Mod(min = 0, max = 10)
  public float maxForce;

  public float t;
  public float x;

  public FlowParticles(VectorField field) {
    this.field = field;
  }

  public void setMaxForce(float maxForce) {
    this.maxForce = maxForce;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

  public void setT(float t) {
    this.t = t;
  }

  @Override
  public void apply(List<Particle> particles) {
    for (Particle p : particles) {
      PVector pos = p.position;
      PVector desired = field.at(pos.x + x, pos.y, pos.z, t).copy();
      //desired.limit(10);

      PVector steer = PVector.sub(desired, p.velocity);
      steer.limit(maxForce);
      steer.mult(weight);

      p.applyForce(steer);
    }
  }
}
