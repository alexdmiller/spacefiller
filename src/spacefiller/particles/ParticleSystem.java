package spacefiller.particles;

import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.math.Vector;

import spacefiller.particles.behaviors.AssymetricParticleBehavior;
import spacefiller.particles.behaviors.LocalBehavior;
import spacefiller.particles.behaviors.SymmetricBehavior;
import spacefiller.particles.sources.AreaSource;
import spacefiller.particles.sources.PointSource;
import spacefiller.particles.sources.Source;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * Created by miller on 7/13/17.
 */
public class ParticleSystem {
  private static ExecutorService pool = Executors.newFixedThreadPool(8);

  public static void setThreadCount(int num) {
    pool = Executors.newFixedThreadPool(num);
  }

  private Bounds bounds;
  private List<Source> sources;
  private List<AssymetricParticleBehavior> assymetricBehaviors;
  private List<SymmetricBehavior> symmetricBehaviors;
  private List<LocalBehavior> localBehaviors;
  private float maxForce = 10;
  private List<ParticleEventListener> particleEventListeners;
  private int maxParticles;
  private HashMap<Integer, Set<Particle>> teamMap;

  private ParticleGrid grid;
  private List<Particle> particleCreationQueue;

  private List<Spring> springs;
  private List<Spring> springCreationQueue;

  private boolean drawDebug = false;

  public ParticleSystem(Bounds bounds, int maxParticles, float cellSize) {
    this.maxParticles = maxParticles;
    this.bounds = bounds;
    this.assymetricBehaviors = new ArrayList<>();
    this.symmetricBehaviors = new ArrayList<>();
    this.localBehaviors = new ArrayList<>();
    this.particleEventListeners = new ArrayList<>();
    this.sources = new ArrayList<>();
    this.teamMap = new HashMap<>();
    this.grid = new ParticleGrid(bounds, cellSize);
    this.particleCreationQueue = new ArrayList<>();
    this.springs = new ArrayList<>();
    this.springCreationQueue = new ArrayList<>();
  }

  public ParticleSystem(Bounds bounds, int maxParticles) {
    this(bounds, maxParticles, 100);
  }

  public ParticleSystem(float width, float height) {
    this(new Bounds(width, height), 8000, 100);
  }

  public ParticleSystem(float width, float height, float cellSize) {
    this(new Bounds(width, height), 8000, cellSize);
    java.lang.System.out.println(width + ", " + height);

  }

  public void update() {
    synchronized (particleCreationQueue) {
      particleCreationQueue.forEach(p -> {
        grid.addParticle(p);

        for (ParticleEventListener eventListener : particleEventListeners) {
          eventListener.particleAdded(p);
        }
      });
    }

    particleCreationQueue.clear();

    springs.addAll(springCreationQueue);
    springCreationQueue.clear();

    updateSprings();
    updateParticleSources();
    updateAsymmetricBehaviors();
    updateSymmetricBehaviors();
    cleanUpParticles();
  }

  private void updateSprings() {
    for (Spring spring : springs) {
      spring.update();
    }
  }

  private void updateParticleSources() {
    if (grid.getParticles().size() < maxParticles) {
      if (sources.size() > 0) {
        // TODO: This is a strange way to spawn particles. Should spread them around to
        // all sources; not just update a single source.

        for (Source source : getSources()) {
          for (int i = 0; i < source.getSpawnRate() && grid.getParticles().size() < maxParticles; i++) {
            Particle p = createParticle(source.generatePoint(), source.getDimension(), source.getTeam());
            p.setRandomVelocity(0.5f, 1, 2);
          }
        }
      }
    }
  }

  private void updateSymmetricBehaviors() {
    List<Callable<Object>> symmetricBehaviorRunnables = new ArrayList<>();
    for (int x = 0; x < grid.getCols() - 1; x++) {
      for (int y = 0; y < grid.getRows() - 1; y++) {
        ParticleGrid.Cell[] neighbors = x > 0 ?
            new ParticleGrid.Cell[]{
                grid.getCell(x + 1, y),
                grid.getCell(x, y + 1),
                grid.getCell(x + 1, y + 1),
                grid.getCell(x - 1, y + 1),
            } :
            new ParticleGrid.Cell[]{
                grid.getCell(x + 1, y),
                grid.getCell(x, y + 1),
                grid.getCell(x + 1, y + 1),
            };

        symmetricBehaviorRunnables.add(Executors.callable(
            new CellRunnable(grid.getCell(x, y), neighbors)));
      }
    }

    try {
      pool.invokeAll(symmetricBehaviorRunnables);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void updateAsymmetricBehaviors() {
    List<Callable<Object>> runnables = new ArrayList<>();
    for (Particle p : grid.getParticles()) {
      if (p.isActive()) {
        runnables.add(Executors.callable(new ParticleRunnable(p)));
      }
    }

    try {
      pool.invokeAll(runnables);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void cleanUpParticles() {
    List<Particle> particles = grid.getParticles();
    for (int i = particles.size() - 1; i >= 0; i--) {
      Particle p = particles.get(i);
      if (p.isRemoveFlag()) {
        grid.removeParticle(p);
        if (teamMap.get(p.getTeam()) != null) {
          teamMap.get(p.getTeam()).remove(p);
        }
      }
    }

    for (int i = springs.size() - 1; i >= 0; i--) {
      Spring s = springs.get(i);
      if (s.removeFlag) {
        springs.remove(i);
        s.getN1().getConnections().remove(s);
        s.getN2().getConnections().remove(s);
      }
    }
  }

  public void setDebugDraw(boolean b) {
    drawDebug = b;
  }

  public List<Particle> getParticlesWithTag(ParticleTag particleTag) {
    return grid.getParticlesWithTag(particleTag);
  }

  public Map<ParticleTag, List<Particle>> getAllTags() {
    return grid.getAllTags();
  }

  public float getDensity(Vector newPosition) {
    if (!bounds.contains(newPosition)) {
      return 1;
    }

    int cx = (int) (Math.floor(newPosition.x / grid.getCellSize()));
    int cy = (int) (Math.floor(newPosition.y / grid.getCellSize()));

    return grid.getCell(cx, cy).getParticles().size()
        / (grid.getCellSize() * grid.getCellSize());
  }

  public float getTotalDensity() {
    float total = 0;

    for (ParticleGrid.Cell cell : grid.getCells()) {
      total += cell.getParticles().size()
          / (grid.getCellSize() * grid.getCellSize());
    }

    return total;
  }

  public int getTotalParticlesFromCells() {
    int total = 0;

    for (ParticleGrid.Cell cell : grid.getCells()) {
      total += cell.getParticles().size();
    }

    return total;
  }

  public void setPosition(Particle particle, Vector vector) {
    int oldHash = grid.hashPosition(particle.getPosition());
    particle.setPosition(vector);
    grid.updateCell(particle, oldHash);
  }

  public ParticleGrid getGrid() {
    return grid;
  }

  public void clearOldParticles(int maxLife) {
    for (Particle p : grid.getParticles()) {
      if (p.getLife() > maxLife) {
        p.setRemoveFlag(true);
      }
    }
  }

  private class CellRunnable implements Runnable {
    private ParticleGrid.Cell cell;
    private ParticleGrid.Cell[] neighbors;

    public CellRunnable(ParticleGrid.Cell cell, ParticleGrid.Cell[] neighbors) {
      this.cell = cell;
      this.neighbors = neighbors;
    }

    @Override
    public void run() {
      for (SymmetricBehavior behavior : symmetricBehaviors) {
        List<Particle> particles = behavior.getTag() == null ?
            cell.getParticles() :
            new ArrayList<>(cell.getParticlesByTag(behavior.getTag()));

        for (int i = 0; i < particles.size(); i++) {
          Particle p1 = particles.get(i);

          if (p1.isActive()) {
            for (int j = i + 1; j < particles.size(); j++) {
              Particle p2 = particles.get(j);

              if (p2.isActive()) {
                behavior.apply(particles.get(i), particles.get(j));
              }
            }
          }
        }

        for (ParticleGrid.Cell neighborCell : neighbors) {
          List<Particle> neighborParticles = behavior.getTag() == null ?
              neighborCell.getParticles() :
              neighborCell.getParticlesByTag(behavior.getTag());
          for (Particle p1 : particles) {
            for (Particle p2 : neighborParticles) {
              behavior.apply(p1, p2);
            }
          }
        }
      }
    }
  }

  private class ParticleRunnable implements Runnable {
    private Particle p;

    public ParticleRunnable(Particle particle) {
      this.p = particle;
    }

    @Override
    public void run() {
      int oldHash = grid.hashPosition(p.getPosition());

      for (LocalBehavior behavior : localBehaviors) {
        if (behavior.getTag() == null || p.hasTag(behavior.getTag())) {
          behavior.apply(p);
        }
      }

      for (AssymetricParticleBehavior behavior : assymetricBehaviors) {
        if (behavior.getTag() == null || p.hasTag(behavior.getTag())) {
          if (behavior.isGlobal()) {
            behavior.apply(p, grid.getParticlesWithTag(behavior.getNeighborFilter()).stream().filter(p2 -> p != p2));
          } else {
            behavior.apply(p, grid.getNeighbors(p.getPosition(), behavior.getNeighborFilter()).filter(p2 -> p != p2));
          }
        }
      }

      p.flushForces(maxForce);
      p.update();

      grid.updateCell(p, oldHash);
    }
  }


  protected void registerTeam(Particle p, int team) {
    if (p.getTeam() != -1) {
      teamMap.get(p.getTeam()).remove(p);
    }

    if (!teamMap.containsKey(team)) {
      teamMap.put(team, new HashSet<>());
    }

    teamMap.get(team).add(p);
  }

  protected void registerTag(Particle p, ParticleTag tag) {
    grid.addTag(p, tag);
  }

  protected void removeTag(Particle p, ParticleTag tag) {
    grid.removeTag(p, tag);
  }

  public Set<Particle> getTeam(int team) {
    return teamMap.get(team);
  }

  public Collection<Set<Particle>> getTeams() {
    return teamMap.values();
  }

  public void setMaxParticles(int maxParticles) {
    this.maxParticles = maxParticles;
  }

  public void registerEventListener(ParticleEventListener particleEventListener) {
    // TODO: allow adding / removing particles
    this.particleEventListeners.add(particleEventListener);
  }

  public Collection<Particle> fillWithParticles(int numParticles, int dimension, int teams) {
    List<Particle> newParticles = new ArrayList<>();
    for (int i = 0; i < numParticles; i++) {
      Particle p = createParticle(bounds.getRandomPointInside(dimension), dimension, -1);
      p.setRandomVelocity(1, 2, dimension);
      p.setTeam((int) (Math.random() * teams));
      newParticles.add(p);
    }
    return newParticles;
  }

  public Collection<Particle> fillWithParticles(int numParticles, int dimension) {
    return fillWithParticles(numParticles, dimension, 1);
  }

  public Collection<Particle> fillWithParticles(int numParticles) {
    return fillWithParticles(numParticles, 2);
  }

  public Particle createParticle(float x, float y, float z) {
    return createParticle(new Vector(x, y, z), 3, -1);
  }

  public Particle createParticle(float x, float y) {
    return createParticle(new Vector(x, y), 2, -1);
  }

  public Particle createParticle(Particle particle) {
    return createParticle(particle.getPosition());
  }

  public LoopData createLoop(float x, float y, float radius, int num, float springLength, float springK, int team) {
    PVector center = new PVector(x, y);
    List<Particle> particles = new ArrayList<>();
    List<Spring> springs = new ArrayList<>();

    for (int i = 0; i < num; i++) {
      float theta = (float) (2f * Math.PI / num) * i;
      Particle p = createParticle(
              (float) (center.x + Math.cos(theta) * radius),
              (float) (center.y + Math.sin(theta) * radius));

      p.setTeam(team);
      particles.add(p);
    }

    Particle first = particles.get(0);
    Particle last = particles.get(particles.size() - 1);

    springs.add(createSpring(first, particles.get(1), springLength, springK));
    springs.add(createSpring(first, last, springLength, springK));

    springs.add(createSpring(last, particles.get(particles.size() - 2), springLength, springK));
    springs.add(createSpring(last, first, springLength, springK));

    for (int i = 1; i < particles.size() - 1; i++) {
      Particle n = particles.get(i);
      springs.add(createSpring(n, particles.get(i - 1), springLength, springK));
      springs.add(createSpring(n, particles.get(i + 1), springLength, springK));
    }

    return new LoopData(particles, springs);
  }

  public static class LoopData {
    public List<Particle> particles;
    public List<Spring> springs;

    public LoopData(List<Particle> particles, List<Spring> springs) {
      this.particles = particles;
      this.springs = springs;
    }
  }

  public Particle createParticle(Vector position) {
    return createParticle(position, 2, -1);
  }

  public Particle createParticle(Vector position, int dimension, int team) {
    Particle p = new Particle(this, position);
    if (team != -1) {
      p.setTeam(team);
    }

    synchronized (particleCreationQueue) {
      particleCreationQueue.add(p);
    }

    return p;
  }

  public Spring createSpring(Particle n1, Particle n2, float length, float k) {
    Spring spring = new Spring(n1, n2, length, k);

    springCreationQueue.add(spring);

    n1.addConnection(spring);
    n2.addConnection(spring);
    return spring;
  }

  public List<Spring> getSprings() {
    return springs;
  }

  public PointSource createPointSource(float x, float y, int spawnRate, int dimension) {
    PointSource pointSource = new PointSource(new Vector(x, y), spawnRate, dimension);
    sources.add(pointSource);
    return pointSource;
  }

  public AreaSource createAreaSource(Vector position, Bounds bounds, int spawnRate, int dimension) {
    AreaSource areaSource = new AreaSource(position, bounds, spawnRate, dimension);
    sources.add(areaSource);
    return areaSource;
  }

  public AreaSource createAreaSource(int spawnRate, int dimension) {
    return createAreaSource(new Vector(0, 0), this.bounds, spawnRate, dimension);
  }

  public List<Source> getSources() {
    return sources;
  }

  public void clear() {
    for (Particle p : grid.getParticles()) {
      p.setRemoveFlag(true);
    }
  }

  public List<Particle> getParticles() {
    return grid.getParticles();
  }

  public void addBehavior(AssymetricParticleBehavior behavior) {
    behavior.setParticleSystem(this);
    this.assymetricBehaviors.add(behavior);
  }

  public void addBehavior(AssymetricParticleBehavior behavior, ParticleTag tag) {
    behavior.setTagConstraint(tag);
    addBehavior(behavior);
  }

  public void addBehavior(AssymetricParticleBehavior behavior, ParticleTag applicationFilter, ParticleTag neighborFilter) {
    behavior.setTagConstraint(applicationFilter);
    behavior.setNeighborFilter(neighborFilter);
    addBehavior(behavior);
  }

  public void addBehavior(LocalBehavior localBehavior, ParticleTag particleTag) {
    localBehavior.setTagConstraint(particleTag);
    addBehavior(localBehavior);
  }

  public void addBehavior(LocalBehavior localBehavior) {
    localBehavior.setParticleSystem(this);
    localBehaviors.add(localBehavior);
  }

  public void addBehavior(SymmetricBehavior behavior) {
    behavior.setParticleSystem(this);
    symmetricBehaviors.add(behavior);
  }

  public void addBehavior(SymmetricBehavior behavior, ParticleTag tag) {
    behavior.setTagConstraint(tag);
    addBehavior(behavior);
  }

  public Bounds getBounds() {
    return bounds;
  }

  public void setBounds(Bounds bounds) {
    this.bounds = bounds;
  }

  public void notifyRemoved(Particle p) {
    for (ParticleEventListener particleEventListener : particleEventListeners) {
      particleEventListener.particleRemoved(p);
    }
  }

  public void disconnect(Particle n1, Particle n2) {
    Spring spring = findSpring(n1, n2);
    if (spring != null) {
      springs.remove(spring);
      n1.getConnections().remove(spring);
      n2.getConnections().remove(spring);
    }
  }

  public Spring findSpring(Particle n1, Particle n2) {
    for (Spring spring : springs) {
      if (spring.other(n1) == n2) {
        return spring;
      }
    }
    return null;
  }

  public Stream<Particle> getNeighbors(Vector position) {
    return grid.getNeighbors(position, null);
  }

  public Stream<Particle> getNeighbors(Vector position, ParticleTag tag) {
    return grid.getNeighbors(position, tag);
  }

  public void draw(PGraphics graphics) {
    if (drawDebug) {
      graphics.stroke(255);
      graphics.strokeWeight(2);

      synchronized (grid) {
        for (int i = 0; i < grid.getParticles().size(); i++) {
          Particle p = grid.getParticles().get(i);
          graphics.point(p.getPosition().x, p.getPosition().y);
        }
      }
    }
  }

  public void printBehaviors() {
    java.lang.System.out.println("SYMMETRIC BEHAVIORS");
    for (SymmetricBehavior symmetricBehavior : symmetricBehaviors) {
      java.lang.System.out.println(symmetricBehavior.getClass().getSimpleName() + " - " + symmetricBehavior.getTag());
    }

    java.lang.System.out.println("ASYMMETRIC BEHAVIORS");
    for (AssymetricParticleBehavior assymetricBehavior : assymetricBehaviors) {
      java.lang.System.out.println(assymetricBehavior.getClass().getSimpleName() + " - " + assymetricBehavior.getTag() + " - " + assymetricBehavior.getNeighborFilter());
    }

    java.lang.System.out.println("LOCAL BEHAVIORS");
    for (LocalBehavior localBehavior : localBehaviors) {
      java.lang.System.out.println(localBehavior.getClass().getSimpleName());
    }
  }
}
