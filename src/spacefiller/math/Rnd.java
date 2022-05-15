package spacefiller.math;

import java.util.Random;

public class Rnd {
  public static Random random = new Random();

  public static void init(Random random) {
    Rnd.random = random;
  }
}
