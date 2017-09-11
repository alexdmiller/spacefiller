package common;

import spacefiller.remote.Mod;

/**
 * Created by miller on 9/3/17.
 */
public class Integrator {
  private float value;

  @Mod
  public float speed;

  public Integrator() {
    value = 0;
    speed = 0.1f;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public float getValue() {
    return value;
  }

  public void update() {
    value += speed;
  }
}
