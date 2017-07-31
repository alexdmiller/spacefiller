package particles;

import processing.core.PVector;

/**
 * Created by miller on 7/31/17.
 */
public class Source {
  private PVector position;
  private int spawnRate;

  public Source(float x, float y, int spawnRate) {
    this(new PVector(x, y), spawnRate);
  }

  public Source(PVector position, int spawnRate) {
    this.position = position;
    this.spawnRate = spawnRate;
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
}
