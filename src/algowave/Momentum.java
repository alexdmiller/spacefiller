package algowave;

import spacefiller.remote.signal.DataReceiver;

public class Momentum extends DataReceiver {
  private float momentum;
  private float friction;

  public Momentum(float friction) {
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
