package spacefiller.particles;

import spacefiller.math.Vector;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by miller on 9/28/16.
 */
public class Particle {
  private Vector position;
  private Vector velocity;
  private boolean removeFlag = false;
  private Vector lastPosition;
  private Vector forces;
  private int team = -1;
  private Map<String, Object> userData;
  private Set<ParticleTag> tags;
  private boolean teleportFlag = false;
  private ParticleSystem system;
  private int life;
  private float internalFriction;
  private float mass = 1f;
  private boolean active = true;
  private Vector queuedPosition = null;

  protected List<Spring> connections;
  protected boolean staticBody;

  public Particle(ParticleSystem system, Vector position) {
    this.setPosition(position);
    this.setVelocity(new Vector());
    this.forces = new Vector();
    this.userData = new HashMap<>();
    this.connections = new ArrayList<>();
    this.tags = new HashSet<>();
    this.system = system;
    this.internalFriction = 1f;
  }

  public Particle(Particle particle) {
    this(particle.system, particle.position.copy());
  }

  public void seek(Vector target) {
    seek(target, 4, 0.1f);
  }

  public void seek(Vector target, float maxSpeed, float maxForce) {
    if (!staticBody) {
      applyForce(ParticleUtils.getSeekVector(this, target, maxSpeed, maxForce));
    }
  }

  public void avoid(Vector target, float maxSpeed) {
    if (!staticBody) {
      applyForce(ParticleUtils.getAvoidanceVector(this, target, maxSpeed, 0.5f));
    }
  }

  public Particle(ParticleSystem system) {
    this(system, new Vector());
  }

  public Particle(ParticleSystem system, float x, float y, float z) {
    this(system, new Vector(x, y, z));
  }

  public Particle(ParticleSystem system, float x, float y) {
    this(system, new Vector(x, y));
  }

  public Particle addTag(ParticleTag tag) {
    tags.add(tag);
    system.registerTag(this, tag);
    return this;
  }

  public boolean hasTag(ParticleTag tag) {
    return tags.contains(tag);
  }

  public void removeTag(ParticleTag tag) {
    tags.remove(tag);
    system.removeTag(this, tag);
  }

  public int getTeam() {
    return team;
  }

  public void setTeam(int team) {
    system.registerTeam(this, team);
    this.team = team;
  }

  public void update() {
    lastPosition = getPosition().copy();

    if (!staticBody) {
      getVelocity().mult(internalFriction);
      getPosition().add(getVelocity());
    }

    life++;
  }

  // finds the closest particle in the immediate neighborhood; faster than method below
  public Particle findClosestNeighbor(ParticleTag tag) {
    float minDist = Float.MAX_VALUE;
    Particle closest = null;
    for (Particle other : system.getNeighbors(getPosition(), tag).collect(Collectors.toList())) {
      float d = Vector.sub(getPosition(), other.getPosition()).magnitude();
      if (d < minDist) {
        minDist = d;
        closest = other;
      }
    }
    return closest;
  }

  // finds closest particle globally; slower than the method above
  public Particle findClosest(ParticleTag tag) {
    float minDist = Float.MAX_VALUE;
    Particle closest = null;
    for (Particle other : system.getParticlesWithTag(tag)) {
      if (other != this) {
        float d = Vector.sub(getPosition(), other.getPosition()).magnitude();
        if (d < minDist) {
          minDist = d;
          closest = other;
        }
      }
    }
    return closest;
  }

  public void applyFriction(float friction) {
    getVelocity().mult(friction);
  }

  public void flushForces(float limit) {
    forces.limit(limit);
    forces.div(mass);
    getVelocity().add(forces);
    forces.x = 0;
    forces.y = 0;
  }

  public void applyForce(Vector force) {
    if (!staticBody) {
      forces.add(force);
    }
  }

  public void applyForce(float fx, float fy) {
    if (!staticBody) {
      forces.add(fx, fy);
    }
  }

  public void setRandomVelocity(float min, float max, int dimension) {
    if (dimension == 3) {
      this.setVelocity(Vector.random3D());
      this.getVelocity().setMag((float) Math.random() * (max - min) + min);
    } else if (dimension == 2) {
      this.setVelocity(Vector.random2D());
      this.getVelocity().setMag((float) Math.random() * (max - min) + min);
    }
  }

  public void setUserData(String key, Object o) {
    userData.put(key, o);
  }

  public boolean hasUserData(String key) {
    return userData.containsKey(key) && userData.get(key) != null;
  }

  public Object getUserData(String key) {
    return userData.get(key);
  }

  public Set<String> getUserDataTags() {
    return userData.keySet();
  }

  public boolean hasTeleported() {
    return teleportFlag;
  }

  public void setTeleportFlag(boolean value) {
    teleportFlag = value;
  }

  public void addConnection(Spring spring) {
    connections.add(spring);
  }

  public List<Spring> getConnections() {
    return connections;
  }

  public void setStatic(boolean value) {
    this.staticBody = value;
  }

  public int getLife() {
    return life;
  }

  public void setInternalFriction(float friction) {
    internalFriction = friction;
  }

  public Set<ParticleTag> getTags() {
    return tags;
  }

  public Stream<Particle> getNeighbors() {
    return system.getNeighbors(getPosition(), null);
  }

  public Stream<Particle> getNeighbors(ParticleTag tag) {
    return system.getNeighbors(getPosition(), tag);
  }

  public void detachSprings() {
    for (Spring s : connections) {
      s.removeFlag = true;
    }
  }

  public void detachSpring(Spring s) {
    s.removeFlag = true;
  }

  public float getMass() {
    return mass;
  }

  public void setMass(float mass) {
    this.mass = mass;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  public Vector getPosition() {
    return position;
  }

  public void setPosition(Vector position) {
    this.position = position.copy();
  }

  public Vector getVelocity() {
    return velocity;
  }

  public void setVelocity(Vector velocity) {
    this.velocity = velocity;
  }

  public boolean isRemoveFlag() {
    return removeFlag;
  }

  public void setRemoveFlag(boolean removeFlag) {
    this.removeFlag = removeFlag;
  }

  public Spring findConnection(Particle p2) {
    for (Spring spring : connections) {
      if (spring.other(this) == p2) {
        return spring;
      }
    }
    return null;
  }


//  public void queuePosition(Vector position) {
//    queuedPosition = position;
//  }

  public Vector getLastPosition() {
    return lastPosition;
  }
}
