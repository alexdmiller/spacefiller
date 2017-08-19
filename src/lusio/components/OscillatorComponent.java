package lusio.components;

import lusio.Lusio;
import processing.core.PGraphics;
import scene.SceneComponent;

public class OscillatorComponent extends SceneComponent {
  private float t;
  private float updateSpeed = 0.1f;
  private int divisions = 100;

  // TODO: why does this need to have a max force?
  public OscillatorComponent() {
  }

  @Override
  public void draw(PGraphics graphics) {
    graphics.strokeWeight(2);
    t += updateSpeed;

    for (int j = 0; j < 5; j++) {
      graphics.rotateY(j + t / 20);
      graphics.rotateX(j + t / 20);

      float angle = 0;
      float lastX = (float) Math.cos(angle) * f(0, t, j);
      float lastY = (float) Math.sin(angle) * f(0, t, j);

      for (int i = 1; i <= divisions; i++) {
        angle = (float) (i * (Math.PI * 2) / divisions);
        float x = (float) Math.cos(angle) * f(angle, t, j);
        float y = (float) Math.sin(angle) * f(angle, t, j);

        graphics.line(lastX, lastY, x, y);

        lastX = x;
        lastY = y;
      }
    }
  }

  private float f(float angle, float t, float r) {
    return Lusio.instance.noise(angle, t / 10 + r / 10, r) * 200  + r * 100 + 50;
  }
}
