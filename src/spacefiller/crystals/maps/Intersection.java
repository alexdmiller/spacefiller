package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class Intersection extends Animator.SimpleAnimator {
  float distance = 50;
  float radius = 150;
  float maxRadius = 50;

  @Override
  public void setup() {

  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    if (radius > maxRadius){
      radius *= 0.99f;
    }

    graphics.blendMode(PConstants.ADD);

    graphics.pushMatrix();
    graphics.translate(graphics.width / 2, graphics.height / 2);

    float theta = frameCount / 100f;
    graphics.fill(255, 0, 0);
    graphics.ellipse((float) Math.cos(theta) * distance, (float) Math.sin(theta) * distance, radius, radius);

    theta += (float) Math.PI * 2 / 3;
    graphics.fill(0, 255, 0);
    graphics.ellipse((float) Math.cos(theta) * distance, (float) Math.sin(theta) * distance, radius, radius);

    theta += (float) Math.PI * 2 / 3;
    graphics.fill(0, 0, 255);
    graphics.ellipse((float) Math.cos(theta) * distance, (float) Math.sin(theta) * distance, radius, radius);

    graphics.popMatrix();
    graphics.blendMode(PConstants.NORMAL);
  }

  @Override
  public void noteOn(int channel, int note) {
    radius += 50;
  }
}
