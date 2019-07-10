package algoplex2.scenes;

import spacefiller.mapping.Grid;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;
import toxi.sim.automata.CAMatrix;
import toxi.sim.automata.CARule2D;

public class LifeScene extends GridScene {
  private float t = 0;
  private int step = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private PerlinNoise perlin;
  private CAMatrix matrix;
  private CARule2D lifeRule;

  private float birthChance;
  private float deathChance;

  public LifeScene() {
    perlin = new PerlinNoise();
  }

  @Override
  public void preSetup(Grid grid) {
    matrix = new CAMatrix(grid.getColumns() * 4, grid.getRows() * 4);

    byte[] birthRules = new byte[] { 3 };
    byte[] survivalRules = new byte[] { 2,3 };

    lifeRule = new CARule2D(birthRules, survivalRules,2,true);
    // assign the rules to the CAMatrix
    matrix.setRule(lifeRule);

    matrix.addNoise(0.9f);

    super.preSetup(grid);
  }

  @Mod
  public void addNoise() {
    matrix.addNoise(0.01f);
  }

  @Mod(min = 0, max = 0.5f)
  public void birthChance(float chance) {
//    System.out.println(num);
//    byte[] birthRules = new byte[Math.round(num)];
//    for (int i = 0; i < num; i++) {
//      birthRules[i] = (byte) i;
//    }
//
//    System.out.println(Arrays.toString(birthRules));
//
//    lifeRule.setBirthRules(birthRules);
    birthChance = chance;
    lifeRule.setRandomProbabilities(birthChance, deathChance);
    lifeRule.randomize();

    while (lifeRule.getBirthRules()[0] == 0) {
      lifeRule.randomize();
    }
  }

  @Mod(min = 0, max = 0.5f)
  public void deathChance(float chance) {
//    System.out.println(num);
//    byte[] birthRules = new byte[Math.round(num)];
//    for (int i = 0; i < num; i++) {
//      birthRules[i] = (byte) i;
//    }
//
//    System.out.println(Arrays.toString(birthRules));
//
//    lifeRule.setBirthRules(birthRules);
    deathChance = chance;
    lifeRule.setRandomProbabilities(birthChance, deathChance);
    lifeRule.randomize();
  }

  @Override
  public void draw(PGraphics graphics) {
      step++;
//    t += speed;
//
//    float gridSize = grid.getCellSize() / 2;
//    for (float x = 0; x < grid.getWidth(); x += gridSize) {
//      for (float y = 0; y < grid.getHeight(); y += gridSize) {
//        if (perlin.noise(x / 500, y / 500, t) > 0.5f) {
//          graphics.noStroke();
//          graphics.fill(255);
//          graphics.rect(x, y, gridSize, gridSize);
//        }
//      }
//    }

      if (step % 10 == 0) {
        matrix.update();
      }

      int[] m = matrix.getMatrix();

      graphics.noStroke();
      graphics.fill(255);
      float cellSize = grid.getWidth() / matrix.getWidth();
      for (int i = 0; i < matrix.getHeight(); i++) {
        for (int j = 0; j < matrix.getWidth(); j++) {
          if (m[i * matrix.getWidth() + j] > 0) {
            graphics.rect(j * cellSize, i * cellSize, cellSize, cellSize);
          }
        }
      }

    super.draw(graphics);
  }

}
