package algoplex2.scenes;

import algoplex2.Grid;
import algoplex2.Quad;
import graph.*;
import processing.core.PConstants;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;
import toxi.sim.automata.CAMatrix;
import toxi.sim.automata.CARule;
import toxi.sim.automata.CARule2D;

import java.util.Arrays;

public class PerlinGridScene extends GridScene {
  private float t = 0;
  private int step = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  public float noiseAmount = 0;

  private PerlinNoise perlin;
  private static final int[][] SQUARE = {{0, 0}, {1, 0}, {1, 1}, {0, 1}};

  public PerlinGridScene() {
    perlin = new PerlinNoise();
  }


  @Override
  public void draw(PGraphics graphics) {
    step++;
    t += speed;

    graphics.fill(255);
    graphics.noFill();
    graphics.stroke(255);
    graphics.strokeWeight(2);

    float gridSize = grid.getCellSize() / 2;
    for (float x = 0; x < grid.getWidth(); x += gridSize) {
      for (float y = 0; y < grid.getHeight(); y += gridSize) {
        if (perlin.noise(x / 500, y / 500, t) > 0.5f) {
          graphics.beginShape();

          for (int i = 0; i < SQUARE.length; i++) {
            drawVertex(x + SQUARE[i][0] * gridSize, y + SQUARE[i][1] * gridSize, graphics);
          }

          graphics.endShape(PConstants.CLOSE);

          graphics.beginShape(PConstants.LINES);
          drawVertex(x, y, graphics);
          drawVertex(x + gridSize, y + gridSize, graphics);
          drawVertex(x + gridSize, y, graphics);
          drawVertex(x, y + gridSize, graphics);
          graphics.endShape();
//          float sx = x + perlin.noise(x, y, t) * noiseAmount;
//          float sy = y + perlin.noise(x, y, t + 100) * noiseAmount;
//          graphics.stroke(255);
//          graphics.strokeWeight(2);
//          graphics.rect(sx, sy, gridSize, gridSize);
//          graphics.line(sx, sy, sx + gridSize, sy + gridSize);
//          graphics.line(sx + gridSize, sy, sx, sy + gridSize);
//
//          graphics.noStroke();
//          graphics.fill(0);
//          graphics.rect(sx, sy, gridSize, gridSize);
        }
      }
    }

    super.draw(graphics);
  }

  private void drawVertex(float x, float y, PGraphics graphics) {
    float nx = x + perlin.noise(x, y, t) * noiseAmount - noiseAmount / 2;
    float ny = y + perlin.noise(x, y, t + 100) * noiseAmount - noiseAmount / 2;
    graphics.fill(perlin.noise(nx / 100, ny / 100, t + 200) * 255);
    graphics.vertex(nx, ny);
  }
}
