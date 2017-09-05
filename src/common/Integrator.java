package common;

/**
 * Created by miller on 9/3/17.
 */
public class Integrator {
  private float value;
  private float speed;

  public Integrator() {
    value = 0;
    speed = 1;
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
