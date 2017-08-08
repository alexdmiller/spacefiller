package particles;

import processing.core.PVector;

/**
 * Created by miller on 7/31/17.
 */
public class Source {
  private PVector position;
  private int spawnRate;
  private int dimension;

  public Source(float x, float y, int spawnRate, int dimension) {
    this(new PVector(x, y), spawnRate, dimension);
  }

  public Source(PVector position, int spawnRate, int dimension) {
    this.position = position;
    this.spawnRate = spawnRate;
    this.dimension = dimension;
  }

  public PVector getPosition() {
    return position;
  }

  public void setPosition(PVector position) {
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
}
