package spacefiller.apps.patchbay;

import spacefiller.patchbay.osc.OscRouter;

public class OscExample {
  public static void main(String[] args) throws InterruptedException {
    OscRouter oscRouter = new OscRouter("127.0.0.1", 12000);
    oscRouter.address("/particle_speed").print();

    while (true) {
      Thread.sleep(10);
    }
  }
}
