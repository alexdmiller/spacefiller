package spacefiller.particles.sources;

import spacefiller.math.Vector;
import spacefiller.particles.Bounds;
import spacefiller.particles.sources.Source;

public class AreaSource implements Source {
  private Vector position;
  private Bounds bounds;
  private int spawnRate;
  private int dimension;
  private int team;

  public AreaSource(Vector position, Bounds bounds, int spawnRate, int dimension) {
    this.position = position;
    this.bounds = bounds;
    this.spawnRate = spawnRate;
    this.dimension = dimension;
    this.team = -1;
  }

  @Override
  public Vector generatePoint() {
    return this.bounds.getRandomPointInside(dimension).add(position);
  }

  @Override
  public int getSpawnRate() {
    return spawnRate;
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  public spacefiller.particles.sources.AreaSource setTeam(int team) {
    this.team = team;
    return this;
  }

  @Override
  public int getTeam() {
    return team;
  }
}
