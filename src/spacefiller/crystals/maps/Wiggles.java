package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class Wiggles extends Animator.DoubleAnimator {
  float height = 0;
  float amp = 20;
  float speed = 0;
  float freq = 1;
  float acceleration = 0;
  float wavePosition = 0;

  @Override
  public void drawMap(PGraphics graphics, int frameCount, List<Integer> notes) {
    height *= 0.98f;
    // amp *= 0.95f;

    amp += acceleration * 10;
    amp *= 0.95f;

    speed += acceleration;
    speed *= 0.97f;
    wavePosition += speed;

    doDrawMap(graphics, height, amp, freq, wavePosition);
  }

  private void doDrawMap(PGraphics graphics, float height, float amp, float freq, float wavePosition) {
    graphics.pushMatrix();
    graphics.beginShape();
    graphics.noStroke();
    graphics.fill(255, 0, 0);
    graphics.translate(0, graphics.height / 2);
    graphics.beginShape();
    drawShape(graphics, height, amp, freq, wavePosition);
    graphics.endShape(PConstants.CLOSE);
    graphics.popMatrix();
  }

  @Override
  public void drawSeed(PGraphics graphics, int frameCount, List<Integer> notes) {
    graphics.pushMatrix();
    graphics.beginShape();
    graphics.stroke(255);
    graphics.noFill();
    graphics.strokeWeight(2);
    graphics.translate(0, graphics.height / 2);
    graphics.beginShape();
    drawShape(graphics, height, amp, freq, wavePosition);
    graphics.endShape(PConstants.CLOSE);
    graphics.popMatrix();
  }

  @Override
  public void preview(PGraphics graphics) {
    doDrawMap(graphics, 100, 20, 0.2f, 0);
  }

  private void drawShape(PGraphics graphics, float height, float amp, float freq, float wavePosition) {
    for (int x = 0; x < graphics.width; x += 5) {
      int y = Math.round(f(x, amp, freq, wavePosition) - height);
      graphics.vertex(x, y);
    }
    for (int x = graphics.width; x >= 0; x -= 5) {
      int y = Math.round(f(x, amp, freq, wavePosition) + height);
      graphics.vertex(x, y);
    }
  }

  private float f(float x, float amp, float freq, float wavePosition) {
    return (float) (Math.sin(x / 10f * freq + wavePosition) * amp);
  }

  @Override
  public void noteOn(int channel, int note) {
    if (channel == 1) {

    } else if (channel == 2) {
      height += 30;
    } else if (channel == 3) {
      acceleration = 0.03f;
      //amp += 20;
    }
  }

  @Override
  public void noteOff(int channel, int note) {
    if (channel == 3) {
      acceleration = 0;
    }
  }
}

