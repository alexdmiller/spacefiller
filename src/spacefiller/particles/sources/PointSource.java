package spacefiller.particles.sources;


import spacefiller.math.Vector;
import spacefiller.particles.sources.Source;

public class PointSource implements Source {
  private Vector position;
  private int spawnRate;
  private int dimension;
  private int team;

  public PointSource(float x, float y, int spawnRate, int dimension) {
    this(new Vector(x, y), spawnRate, dimension);
  }

  public PointSource(Vector position, int spawnRate, int dimension) {
    this.position = position;
    this.spawnRate = spawnRate;
    this.dimension = dimension;
    this.team = -1;
  }

  public Vector getPosition() {
    return position;
  }

  public void setPosition(Vector position) {
    this.position = position;
  }

  public int getSpawnRate() {
    return spawnRate;
  }

  public void setSpawnRate(int spawnRate) {
    this.spawnRate = spawnRate;
  }

  public int getDimension() {
    return dimension;
  }

  public spacefiller.particles.sources.PointSource setTeam(int team) {
    this.team = team;
    return this;
  }

  @Override
  public int getTeam() {
    return team;
  }

  @Override
  public Vector generatePoint() {
    return getPosition().copy();
  }
}
