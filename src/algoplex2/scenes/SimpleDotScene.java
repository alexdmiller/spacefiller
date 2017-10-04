package algoplex2.scenes;

import spacefiller.remote.Mod;
import processing.core.PGraphics;

public class SimpleDotScene extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  public float lineResolution = 10;
  public float dotResolution = 5;

  @Override
  public void draw(PGraphics graphics) {
    t += speed;
    graphics.stroke(255);
    graphics.strokeWeight(2);
    for (float x = 0; x < grid.getWidth(); x += grid.getCellSize() / dotResolution) {
      for (float y = 0; y < grid.getHeight(); y += grid.getCellSize() / dotResolution) {
        graphics.point(x,(y + t * 100) % grid.getHeight());
      }
    }

    graphics.stroke(255);
    graphics.strokeWeight(2);
    for (float x = grid.getCellSize() / dotResolution / 2; x < grid.getWidth(); x += grid.getCellSize() / dotResolution) {
      for (float y = 0; y < grid.getHeight(); y += grid.getCellSize() / dotResolution) {
        graphics.point(x,(y + t * 200) % grid.getHeight());
      }
    }

    super.draw(graphics);
  }
}
