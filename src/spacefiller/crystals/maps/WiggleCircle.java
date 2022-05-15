package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class WiggleCircle extends Animator.DoubleAnimator {
  float height = 0;
  float amp = 0;
  float amp2 = 0;
  float speed = 0;
  float freq = 1;
  float acceleration = 0;
  float wavePosition = 0;

  float speed2 = 0;
  float wavePosition2 = 0;

  @Override
  public void drawMap(PGraphics graphics, int frameCount, List<Integer> notes) {
    height *= 0.98f;

    amp += acceleration * 10;
    amp *= 0.95f;
    amp2 *= 0.9f;

    speed += acceleration;
    speed *= 0.97f;
    wavePosition += speed;

    speed *= 0.97f;
    wavePosition2 += speed2;

    doMapDraw(graphics, height, amp, amp2, wavePosition, wavePosition2);
  }

  private void doMapDraw(
      PGraphics graphics,
      float height,
      float amp,
      float amp2,
      float wavePosition,
      float wavePosition2) {
    graphics.pushMatrix();
    graphics.beginShape();
    graphics.noStroke();
    graphics.fill(255, 0, 0);
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.beginShape();
    drawShape(graphics, height, amp, amp2, wavePosition, wavePosition2);
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
    graphics.translate(graphics.width / 2, graphics.height / 2);
    graphics.beginShape();
    drawShape(graphics, height, amp, amp2, wavePosition, wavePosition2);
    graphics.endShape(PConstants.CLOSE);
    graphics.popMatrix();
  }

  @Override
  public void preview(PGraphics graphics) {
    doMapDraw(graphics, 100, 10, 5, 0, 0);
  }

  private void drawShape(
      PGraphics graphics,
      float height,
      float amp,
      float amp2,
      float wavePosition,
      float wavePosition2) {
    for (float theta = 0; theta < 2 * Math.PI; theta += Math.PI / 20f) {
      float r = Math.max(0, f(theta, height, amp, amp2, wavePosition, wavePosition2));
      float x = (float) (Math.cos(theta) * r);
      float y = (float) (Math.sin(theta) * r);
      graphics.vertex(x, y);
    }
  }

  private float f(
      float x,
      float height,
      float amp,
      float amp2,
      float wavePosition,
      float wavePosition2) {
    return (float) (height + Math.sin(x * 5 * freq + wavePosition) * amp + Math.sin(x * 8 - wavePosition2) * amp2);
  }

  @Override
  public void noteOn(int channel, int note) {
    if (channel == 1) {
      amp2 += 5;
      speed2 += 0.1;
    } else if (channel == 2) {
      height += 40;
    } else if (channel == 3) {
      acceleration = 0.02f;
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

