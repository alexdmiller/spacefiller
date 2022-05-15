package spacefiller.particles;

import spacefiller.math.Rnd;
import spacefiller.math.Vector;

import java.io.Serializable;

public class Bounds implements Serializable {
  Vector topBackLeft;
  Vector bottomFrontRight;

  public Bounds(float width, float height, float depth) {
    this.topBackLeft = new Vector(0, 0, 0);
    this.bottomFrontRight = new Vector(width, height, depth);
  }

  public Bounds(float width, float height) {
    this.topBackLeft = new Vector(0, 0, 0);
    this.bottomFrontRight = new Vector(width, height, 0);
  }

  public Bounds(float size) {
    this(size, size, size);
  }

//  public boolean contains(double x, double y) {
//    return (x > -width / 2 && x < width / 2) &&
//        (y > -height / 2 && y < height / 2);
//  }

  public boolean contains(float x, float y) {
    return x >= topBackLeft.x && y >= topBackLeft.y && x <= bottomFrontRight.x && y <= bottomFrontRight.y;
  }

  public boolean contains(float x, float y, float z) {
    return x >= topBackLeft.x && y >= topBackLeft.y && z >= topBackLeft.z &&
        x <= bottomFrontRight.x && y <= bottomFrontRight.y && z <= bottomFrontRight.z;
  }

  public boolean contains(Vector p) {
    return contains(p.x, p.y, p.z);
  }

  public void constrain(Particle p) {
    if (p.getPosition().x < topBackLeft.x) {
      p.getPosition().x = topBackLeft.x + 1;
      p.getVelocity().x *= -1;
    } else if (p.getPosition().x > bottomFrontRight.x) {
      p.getPosition().x = bottomFrontRight.x - 1;
      p.getVelocity().x *= -1;
    }

    if (p.getPosition().y < topBackLeft.y) {
      p.getPosition().y = topBackLeft.y + 1;
      p.getVelocity().y *= -1;
    } else if (p.getPosition().y > bottomFrontRight.y) {
      p.getPosition().y = bottomFrontRight.y - 1;
      p.getVelocity().y *= -1;
    }

    if (p.getPosition().z < topBackLeft.z) {
      p.getPosition().z = topBackLeft.z;
      p.getVelocity().z *= -1;
    } else if (p.getPosition().z > bottomFrontRight.z) {
      p.getPosition().z = bottomFrontRight.z;
      p.getVelocity().z *= -1;
    }
  }

  public Vector getTopBackLeft() {
    return topBackLeft;
  }

  public Vector getBottomFrontRight() {
    return bottomFrontRight;
  }

  public Vector getRandomPointInside(int dimension) {
    return new Vector(
        (float) Rnd.random.nextDouble() * getWidth() + topBackLeft.x,
        (float) Rnd.random.nextDouble() * getHeight() + topBackLeft.y,
        dimension == 3 ? (float) Rnd.random.nextDouble() * getHeight() + topBackLeft.z : 0);
  }

  public float getWidth() {
    return bottomFrontRight.x - topBackLeft.x;
  }

  public float getHeight() {
    return bottomFrontRight.y - topBackLeft.y;
  }

  public float getDepth() {
    return bottomFrontRight.z - topBackLeft.z;
  }

  public Vector getCenter() {
    return new Vector(
        topBackLeft.x + getWidth() / 2, topBackLeft.y + getHeight() / 2, topBackLeft.z + getDepth() / 2
    );
  }
}
