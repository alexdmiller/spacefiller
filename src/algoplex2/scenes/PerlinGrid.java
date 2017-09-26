package algoplex2.scenes;

import algoplex2.Quad;
import graph.*;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.math.noise.PerlinNoise;

public class PerlinGrid extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  private PerlinNoise perlin;

  public PerlinGrid() {
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    float gridSize = grid.getCellSize() / 2;
    for (float x = 0; x < grid.getWidth(); x += gridSize) {
      for (float y = 0; y < grid.getHeight(); y += gridSize) {
        if (perlin.noise(x / 500, y / 500, t) > 0.5f) {
          graphics.noStroke();
          graphics.fill(255);
          graphics.rect(x, y, gridSize, gridSize);
        }
      }
    }
    super.draw(graphics);
  }

}
