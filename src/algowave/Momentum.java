package algowave;

import spacefiller.remote.Mod;
import spacefiller.remote.signal.DataReceiver;

public class Momentum extends DataReceiver {
  private float momentum;

  @Mod
  public float friction;

  public Momentum(float friction) {
    this.friction = friction;
  }

  public void kill() {
    momentum = 0;
  }

  public void setFriction(float friction) {
    this.friction = friction;
  }

  @Override
  public void setValue(Object object, boolean normalized) {
    momentum += (float) object;
  }

  @Override
  public void update() {
    momentum *= friction;

    super.setValue(momentum, false);
  }
}
