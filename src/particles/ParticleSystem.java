package particles;

import particles.behaviors.ReflectiveBounds;
import particles.behaviors.ParticleBehavior;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleSystem {
  private Bounds bounds;
  private List<Particle> particles;
  private List<Source> sources;
  private List<ParticleBehavior> behaviors;
  private float maxForce = 10;
  private List<ParticleEventListener> particleEventListeners;
  private int maxParticles = 200;

  public ParticleSystem(Bounds bounds, int maxParticles) {
    this.maxParticles = maxParticles;
    this.bounds = bounds;
    this.particles = new ArrayList<>();
    this.behaviors = new ArrayList<>();
    this.particleEventListeners = new ArrayList<>();
    this.sources = new ArrayList<>();
  }

  public void setMaxParticles(int maxParticles) {
    this.maxParticles = maxParticles;
  }

  public void registerEventListener(ParticleEventListener particleEventListener) {
    // TODO: allow adding / removing particles
    this.particleEventListeners.add(particleEventListener);
  }

  public void fillWithParticles(int numParticles, int dimension) {
    for (int i = 0; i < numParticles; i++) {
      Particle p = createParticle(bounds.getRandomPointInside(dimension), dimension);
      p.setRandomVelocity(1, 2, dimension);
    }
  }

  public Particle createParticle(PVector position, int dimension) {
    Particle p = new Particle(position);
    p.setRandomVelocity(1, 2, dimension);
    particles.add(p);

    for (ParticleEventListener eventListener : particleEventListeners) {
      eventListener.particleAdded(p);
    }

    return p;
  }

  public void createSource(float x, float y, int spawnRate, int dimension) {
    sources.add(new Source(new PVector(x, y), spawnRate, dimension));
  }

  public List<Source> getSources() {
    return sources;
  }

  public void update() {
    if (particles.size() < maxParticles) {
      Source source = sources.get((int) Math.floor(Math.random() * sources.size()));
      for (int i = 0; i < source.getSpawnRate() && particles.size() < maxParticles; i++) {
        createParticle(source.getPosition().copy(), source.getDimension());
      }
    }

    for (ParticleBehavior behavior : behaviors) {
      behavior.apply(particles);
    }

    for (Particle p : particles) {
      p.flushForces(maxForce);
      p.update();
    }
  }

  public List<Particle> getParticles() {
    return particles;
  }

  public void addBehavior(ParticleBehavior behavior) {
    behavior.setParticleSystem(this);
    this.behaviors.add(behavior);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }
}
