package common;

import processing.opengl.PJOGL;
import scene.SceneApplet;

/**
 * Created by miller on 9/17/17.
 */
public class TestSceneApplet extends SceneApplet {
  public static void main(String[] args) {
    main("common.TestSceneApplet");
  }

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

  @Override
  public final void setup() {
    addScene(new TestContourScene());
    super.setup();
  }

  @Override
  public final void draw() {
    background(0);
    super.draw();
  }
}
