package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Kernel;
import processing.core.*;
import processing.event.KeyEvent;

import static processing.core.PConstants.SHIFT;

public class KernelEditor extends Component {
  public static final int DRAW_SCALE = 21;

  private Kernel kernel;

  enum EditMode {
    ADJUST_CELL, COPY_LAST_VALUE;
  }

  enum SymmetryMode {
    NONE, ROTATIONAL, REFLECT_X, REFLECT_Y;
  }
  private SymmetryMode[] symmetries = new SymmetryMode[] {
          SymmetryMode.NONE,
          SymmetryMode.ROTATIONAL,
          SymmetryMode.REFLECT_X,
          SymmetryMode.REFLECT_Y
  };
  private int symmetryModeIndex = 0;

  private EditMode editMode = EditMode.ADJUST_CELL;

  public KernelEditor(PApplet applet) {
    super(applet);

    this.kernel = new Kernel(7, applet);
  }

  @Override
  public void doDraw() {
    // active/current cell
    int activeCellX = (int) Math.floor(mouse.x / DRAW_SCALE);
    int activeCellY = (int) Math.floor(mouse.y / DRAW_SCALE);

    int buddyX = 0;
    int buddyY = 0;

    if (mouseDown) {
      switch (editMode) {
        case ADJUST_CELL: {
          activeCellX = (int) Math.floor(lastMouseClick.x / DRAW_SCALE);
          activeCellY = (int) Math.floor(lastMouseClick.y / DRAW_SCALE);
          break;
        }
      }
    }

    // find your buddy
    if (symmetries[symmetryModeIndex].equals(SymmetryMode.ROTATIONAL)) {
      buddyX = kernel.getSize() - activeCellX - 1;
      buddyY = kernel.getSize() - activeCellY - 1;
    } else if (symmetries[symmetryModeIndex].equals(SymmetryMode.REFLECT_Y)) {
      buddyX = activeCellX;
      buddyY = kernel.getSize() - activeCellY - 1;
    } else if (symmetries[symmetryModeIndex].equals(SymmetryMode.REFLECT_X)) {
      buddyX = kernel.getSize() - activeCellX - 1;
      buddyY = activeCellY;
    }

    if (mouseDown) {
      switch (editMode) {
        case ADJUST_CELL: {
            int x = (int) Math.floor((lastMouseClick.x) / DRAW_SCALE);
            int y = (int) Math.floor((lastMouseClick.y) / DRAW_SCALE);
            float mouseDist = (lastMouse.y - mouse.y) / 10;
            kernel.addToCell(x, y, mouseDist);

            if (!symmetries[symmetryModeIndex].equals(SymmetryMode.NONE)) {
              kernel.setCell(buddyX, buddyY, kernel.getCell(x, y));
            }
          }
          break;
        case COPY_LAST_VALUE: {
            if (mouseInside()) {
              int copyX = (int) Math.floor((lastMouseClick.x) / DRAW_SCALE);
              int copyY = (int) Math.floor((lastMouseClick.y) / DRAW_SCALE);
              int x = (int) Math.floor(mouse.x / DRAW_SCALE);
              int y = (int) Math.floor(mouse.y / DRAW_SCALE);

              kernel.setCell(x, y, kernel.getCell(copyX, copyY));

              if (!symmetries[symmetryModeIndex].equals(SymmetryMode.NONE)) {
                kernel.setCell(buddyX, buddyY, kernel.getCell(copyX, copyY));
              }
            }
          }
          break;
      }
    }

    applet.image(kernel.getRendered(), 0, 0, kernel.getSize() * DRAW_SCALE, kernel.getSize() * DRAW_SCALE);

    //if (renderNumbers) {
    //  for (int x = 0; x < kernel.width; x++) {
    //    for (int y = 0; y < kernel.height; y++) {
    //      text(round(brightness(kernel.pixels[y * kernel.width + x]) / 255f * 10), x * DRAW_SCALE, y * DRAW_SCALE + DRAW_SCALE);
    //    }
    //  }
    //}

    applet.stroke(150);
    applet.noFill();
    applet.rect(0, 0, kernel.getSize() * DRAW_SCALE, kernel.getSize() * DRAW_SCALE);

    for (int x = 0; x < kernel.getSize(); x++) {
      applet.line(x * DRAW_SCALE, 0, x* DRAW_SCALE, kernel.getSize() * DRAW_SCALE);
    }

    for (int y = 0; y < kernel.getSize(); y++) {
      applet.line(0, y * DRAW_SCALE, kernel.getSize() * DRAW_SCALE, y * DRAW_SCALE);
    }

    float centerX = kernel.getSize() / 2 * DRAW_SCALE;
    float centerY = kernel.getSize() / 2 * DRAW_SCALE;

    applet.stroke(255);
    applet.line(
        centerX + DRAW_SCALE / 2,
        centerY + 5,
        centerX + DRAW_SCALE / 2,
        centerY + DRAW_SCALE - 5
    );
    applet.line(
        centerX + 5,
        centerY + DRAW_SCALE / 2,
        centerX + DRAW_SCALE - 5,
        centerY + DRAW_SCALE / 2
    );

    applet.rect(0, 0, getWidth(), getHeight());

    if (mouseInside()) {
      drawCursors(buddyX, buddyY, activeCellX, activeCellY);
    }

    drawSymmetryMenu();
  }

  private void drawCursors(int buddyX, int buddyY, int activeCellX, int activeCellY) {
    applet.strokeWeight(2);
    if (!symmetries[symmetryModeIndex].equals(SymmetryMode.NONE)) {
      // buddy cursor
      applet.stroke(0, 255, 255);
      applet.noFill();
      applet.rect(buddyX * DRAW_SCALE, buddyY * DRAW_SCALE, DRAW_SCALE, DRAW_SCALE);
    }

    // current cell cursor
    applet.stroke(255, 0, 255);
    applet.noFill();
    applet.rect(activeCellX * DRAW_SCALE, activeCellY * DRAW_SCALE, DRAW_SCALE, DRAW_SCALE);

    applet.strokeWeight(1);
  }

  private void drawSymmetryMenu() {
    int originX = kernel.getSize() * DRAW_SCALE + 20;
    int originY = 0;

    applet.textSize(13);
    applet.textAlign(PConstants.LEFT, PConstants.CENTER);
    applet.noStroke();

    for (int i = 0; i < symmetries.length; i += 1) {
      if (i == symmetryModeIndex) {
        applet.fill(255, 255, 0);
      } else {
        applet.fill(40);
      }
      applet.rect(originX, originY, 86, 20);

      // add label
      if (i == symmetryModeIndex) {
        applet.fill(0);
      } else {
        applet.fill(255);
      }
      applet.text(symmetries[i].name(), originX + 2, originY + 14);

      originY += 23;
    }
  }

  @Override
  public float getWidth() {
    return kernel.getSize() * DRAW_SCALE;
  }

  @Override
  public float getHeight() {
    return kernel.getSize() * DRAW_SCALE;
  }

  public void keyPressed(KeyEvent event) {
    if (event.getKeyCode() == 9) {
      if (symmetryModeIndex < symmetries.length - 1) {
        symmetryModeIndex += 1;
      } else {
        symmetryModeIndex = 0;
      }
    }
    if (event.isAltDown()) {
      editMode = EditMode.COPY_LAST_VALUE;
    }
  }

  public void keyReleased(KeyEvent event) {
    if (event.isAltDown()) {
      editMode = EditMode.ADJUST_CELL;
    }
  }
  
  public void setKernel(Kernel kernel) {
    this.kernel = kernel;
  }
}