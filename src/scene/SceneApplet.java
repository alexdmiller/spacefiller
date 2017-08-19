package scene;

import processing.core.PApplet;
import processing.opengl.PJOGL;

import java.util.ArrayList;
import java.util.List;

public class SceneApplet extends PApplet {
  public static int WIDTH = 1920;
  public static int HEIGHT = 1080;

  protected Scene currentScene;
  protected int currentSceneIndex;
  // protected List<Scene> scenes;

  public void settings() {
    // fullScreen(2);
    size(1920, 1080, P3D);
    PJOGL.profile = 1;
  }

//  public final void setup() {
//    scenes = new ArrayList<>();
//    switchScene(0);
//  }

//  public final void draw() {
//    if (currentScene != null) {
//      currentScene.draw(this.getGraphics());
//    }
//  }

}
