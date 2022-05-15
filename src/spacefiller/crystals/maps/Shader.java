package crystal.maps;

import spacefiller.crystals.engine.Animator;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.List;

public class Shader extends Animator.SimpleAnimator {
  PShader shader;

  public Shader(PApplet applet, String shaderName) {
    shader = applet.loadShader(shaderName);
  }

  @Override
  public void draw(PGraphics graphics, int frameCount, List<Integer> notes, float scale) {
    graphics.shader(shader);
    shader.set("resolution", (float) graphics.width, (float) graphics.height);
    shader.set("frame", frameCount);
    graphics.rect(0, 0, graphics.width, graphics.height);
    graphics.resetShader();
  }
}
