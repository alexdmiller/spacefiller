package algoplex2.scenes;

import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.mapping.Quad;
import spacefiller.remote.Mod;

public class PyramidScene extends GridScene {
  private float t;
  private static int NUM_SQUARES = 5;

  @Mod(min = 0, max = (float) Math.PI / 10)
  public float rotX;

  @Mod(min = 0, max = (float) Math.PI / 10)
  public float rotY;

  @Mod(min = 0, max = (float) Math.PI / 10)
  public float rotZ;

  @Mod(min = 0, max = 100)
  public float amplitude = 0;

  @Mod(min = 0, max = 0.05f)
  public float speed = 0.01f;

  @Override
  public void draw(PGraphics graphics) {
    t += speed;
    graphics.stroke(255);
    graphics.noFill();
    graphics.ortho();

    for (Quad square : grid.getSquares()) {
      graphics.pushMatrix();

      graphics.translate(square.getCenter().position.x, square.getCenter().position.y);
      graphics.rotateX(rotX);
      graphics.rotateY(rotY);
      graphics.rotateZ(rotZ);

      float size = square.getWidth() / 2;

      PVector center = new PVector((float) (Math.cos(t) * amplitude), (float) (Math.sin(t) * amplitude), 0);

      graphics.beginShape();
      graphics.vertex(-size, -size, -size);
      graphics.vertex(size, -size, -size);
      graphics.vertex(center.x, center.y, size);

      graphics.vertex(size, -size, -size);
      graphics.vertex(size, size, -size);
      graphics.vertex(center.x, center.y, size);

      graphics.vertex(size, size, -size);
      graphics.vertex(-size, size, -size);
      graphics.vertex(center.x, center.y, size);

      graphics.vertex(-size, size, -size);
      graphics.vertex(-size, -size, -size);
      graphics.vertex(center.x, center.y, size);
      graphics.endShape();
      graphics.popMatrix();
    }

    super.draw(graphics);
  }
}
