package crystal.maps;

import spacefiller.crystals.engine.Animator;
import geomerative.RG;
import geomerative.RShape;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import spacefiller.graphics.Mask;

import java.util.List;

public class Door extends Animator.DoubleAnimator {
  private int t = 0;
  private RShape screen;

  public Door(PApplet parent, int width, int height) {
    screen = RShape.createRectangle(0, 0, width, height);
  }

  @Override
  public void drawMap(PGraphics map, int frameCount, List<Integer> notes) {
    t = frameCount % 500;

    map.fill(0, 0, 255);
    map.ellipse(map.width / 2, map.height / 2 - 100 + t / 10f, 75, 75);

    map.fill(0, 255, 0);
    map.noStroke();
    map.rectMode(PConstants.CORNERS);
    map.rect(0, map.height / 2, map.width, map.height);

    float doorWidth = 100 + t;
    float doorHeight = 200 + t * 2;
    RShape door = RShape.createRectangle(map.width / 2 - doorWidth / 2, map.height / 2 - doorHeight / 2, doorWidth, doorHeight);
    RShape hole = RG.diff(screen, door);
    hole.setFill(0xff0000);
    hole.setStroke(false);
    hole.draw(map);
  }

  @Override
  public void drawSeed(PGraphics seed, int frameCount, List<Integer> notes) {
    t = frameCount % 500;

    seed.stroke(255);
    seed.noFill();
    seed.ellipse(seed.width / 2, seed.height / 2 - 100 + t / 10f, 75, 75);

    seed.fill(0);
    seed.rectMode(PConstants.CORNERS);
    seed.rect(0, seed.height / 2, seed.width, seed.height);

    float doorWidth = 100 + t;
    float doorHeight = 200 + t * 2;
    RShape door = RShape.createRectangle(seed.width / 2 - doorWidth / 2, seed.height / 2 - doorHeight / 2, doorWidth, doorHeight);
    RShape hole = RG.diff(screen, door);
    hole.setFill(0x000000);
    hole.setStroke(0xffffff);
    hole.draw(seed);
  }
}
