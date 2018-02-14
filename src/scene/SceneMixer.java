package scene;

import processing.core.PGraphics;
import processing.core.PImage;
import spacefiller.remote.Mod;

import java.util.ArrayList;
import java.util.List;

public class SceneMixer {
  protected Scene currentScene;
  protected int currentSceneIndex;
  protected List<Scene> scenes;
  protected PGraphics canvas;

  public SceneMixer() {
    scenes = new ArrayList<>();
  }

  public void beginDraw() {
    canvas.beginDraw();
  }

  public void endDraw() {
    canvas.endDraw();
  }

  public PImage getFrame() {
    return canvas;
  }

  public int getOutputWidth() {
    return canvas.width;
  }

  public int getOutputHeight() {
    return canvas.height;
  }

  public void setOutput(PGraphics canvas) {
    this.canvas = canvas;
  }

  public void draw() {
    canvas.background(0);
    if (currentScene != null) {
      currentScene.draw(this.canvas);
    }
  }

  public void switchScene(int sceneIndex) {
    if (currentScene != null && currentScene.alwaysReset()) {
      currentScene.teardown();
    }

    if (sceneIndex < scenes.size()) {
      Scene scene = scenes.get(sceneIndex);
      currentSceneIndex = sceneIndex;

      if (!scene.isSetup() || scene.alwaysReset()) {
        scene.setup();
      }

      currentScene = scene;
    }
  }

  public void gotoNextScene() {
    switchScene((currentSceneIndex + 1) % scenes.size());
  }

  public void addScene(Scene scene) {
    scene.setDimensions(this.canvas.width, this.canvas.height);
    scenes.add(scene);
  }

  public final void addAllScenes(Scene[] sceneArray) {
    for (int i = 0; i < sceneArray.length; i++) {
      addScene(sceneArray[i]);
    }
  }
}
