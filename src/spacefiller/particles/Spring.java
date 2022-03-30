package spacefiller.particles;

import spacefiller.math.Vector;

public class Spring {
  public boolean removeFlag;
  private Particle n1, n2;
  private float springLength;
  private float k;

  public Spring(Particle n1, Particle n2, float springLength, float k) {
    this.n1 = n1;
    this.n2 = n2;
    this.springLength = springLength;
    this.k = k;
  }

  public void update() {
    Vector delta = Vector.sub(n1.getPosition(), n2.getPosition());
    float displacement = (float) ((springLength - delta.magnitude()) * k);
    delta.normalize();
    delta.mult(displacement);
    n1.getVelocity().add(delta);
    n2.getVelocity().sub(delta);
  }

  public Particle getN1() {
    return n1;
  }

  public Particle getN2() {
    return n2;
  }

  public void setN2(Particle p) {
    n2 = p;
  }

  public float getSpringLength() {
    return springLength;
  }

  public void setSpringLength(float springLength) {
    this.springLength = springLength;
  }

  public Particle other(Particle n) {
    if (n1 == n) {
      return n2;
    } else if (n2 == n) {
      return n1;
    } else {
      return null;
    }
  }

  public String toString() {
    return n1.toString() + " <-> " + n2.toString();
  }
}