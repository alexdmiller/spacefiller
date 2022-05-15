package spacefiller.apps.crystals.ui;

import crystal.Loaders;
import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.EngineState;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.io.IOException;

public class EngineStatePicker extends Component {
  private static final int COLS = 8;

  private int width;
  private Engine engine;
  private EngineState[] engineStates;

  public EngineStatePicker(PApplet applet, Engine engine, int width) {
    super(applet);
    this.engine = engine;
    this.width = width;
    engineStates = engine.getEngineStates();
  }

  @Override
  protected void doDraw() {
    int cellWidth = width / COLS;
    int cellHeight = Math.round((9f / 16f) * cellWidth);
    for (int i = 0; i < engineStates.length; i++) {
      int x = i % COLS;
      int y = i / COLS;
      applet.pushMatrix();
      applet.noFill();
      applet.stroke(100);
      applet.strokeWeight(1);
      applet.translate(x * cellWidth, y * cellHeight);

      if (engineStates[i].preview != null && engineStates[i].preview.image != null) {
        applet.image(engineStates[i].preview.image, 0, 0, cellWidth, cellHeight);
      }

      applet.rect(0, 0, cellWidth, cellHeight);
      applet.fill(0);
      applet.noStroke();;
      applet.rect(1, 1, 15, 15);
      applet.fill(255);
      applet.textAlign(PConstants.LEFT, PConstants.TOP);
      applet.text(i, 1, 1);
      applet.popMatrix();
    }

    int selectedX = engine.getStateIndex() % COLS;
    int selectedY = engine.getStateIndex() / COLS;
    applet.noFill();
    if (engine.getState().equals(engineStates[engine.getStateIndex()])) {
      applet.stroke(255);
    } else {
      applet.stroke(255, 0, 0);
    }
    applet.rect(selectedX * cellWidth, selectedY * cellHeight, cellWidth, cellHeight);
  }

  @Override
  public void mousePressed(PVector mouse) {
    super.mousePressed(mouse);

    int imageWidth = width / COLS;
    int imageHeight = (int) (imageWidth * (1080f / 1920));

    int stateIndex = (int) (mouse.y / imageHeight) * COLS + (int) (mouse.x / imageWidth);

    if (stateIndex < engineStates.length) {
      engine.setStateIndex(stateIndex);
    }
  }

  @Override
  public void keyPressed(KeyEvent event) {
    super.keyPressed(event);

    if (event.isMetaDown()) {
      switch (event.getKey()) {
        case 's':
          saveEngineState();
          break;
      }
    }
  }

  @Override
  public float getWidth() {
    return width;
  }

  @Override
  public float getHeight() {
    int cellWidth = width / COLS;
    int cellHeight = Math.round((9f / 16f) * cellWidth);
    int rows = engine.getEngineStates().length / COLS;
    return rows * cellHeight;
  }

  public void saveEngineState() {
    EngineState currentState = engineStates[engine.getStateIndex()];
    currentState.set(engine.getState());
    currentState.preview = engine.getSnapshot();
    currentState.preview.onResolve = () -> {
      try {
        Loaders.saveEngineStates(engineStates);
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }
}
