package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Engine;
import spacefiller.crystals.engine.Kernel;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;

import javax.management.RuntimeErrorException;
import java.lang.management.RuntimeMXBean;

public class KernelSelector extends Component {
  private Engine engine;
  private int scale;
  private int cellSize;

  private int lastClickX;
  private int lastClickY;
  private boolean optionDown;

  private enum Mode {
    NONE,
    SELECT,
    DRAG
  }

  private Mode currentMode = Mode.NONE;

  private static class Selection {
    int x1;
    int y1;
    int x2;
    int y2;

    public boolean inside(int x, int y) {
      return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }

    public Selection translated(int x, int y) {
      Selection selection = new Selection();
      selection.x1 = x1 + x;
      selection.y1 = y1 + y;
      selection.x2 = x2 + x;
      selection.y2 = y2 + y;
      return selection;
    }

    public int width() {
      return x2 - x1 + 1;
    }

    public int height() {
      return y2 - y1 + 1;
    }

    @Override
    public String toString() {
      return "<" + x1 + ", " + y1 + ">, <" + x2 + ", " + y2 + ">";
    }
  }

  private Selection selection = null;

  public KernelSelector(PApplet applet, Engine engine, int scale) {
    super(applet);

    if (engine == null) {
      throw new Error("Kernels cannot be null");
    }
    this.engine = engine;
    this.scale = scale;
    this.cellSize = scale * engine.getKernels()[0][0].getSize();
  }

  @Override
  protected void doDraw() {
    Kernel[][] kernels = engine.getKernels();
    for (int pack = 0; pack < kernels.length; pack++) {
      for (int k = 0; k < kernels[pack].length; k++) {
         applet.image(
             kernels[pack][k].getRendered(),
             k * cellSize, pack * cellSize,
             cellSize, cellSize);
      }
    }

    int x = (int) (mouse.x / cellSize);
    int y = (int) (mouse.y / cellSize);

    switch (currentMode) {
      case DRAG:
        if (selection != null) {
          int dx = x - lastClickX;
          int dy = y - lastClickY;
          Selection translated = selection.translated(dx, dy);
          drawSelection(translated);
        }
        break;
      case SELECT:
        if (selection != null) {
          selection.x2 = Math.min(Math.max(0, x), cols());
          selection.y2 = Math.min(Math.max(0, y), rows());
        }
        break;
    }

    if (selection != null) {
      drawSelection(selection);
    }

//    if (mouseInside()) {
//      int x = (int) (mouse.x / cellSize);
//      int y = (int) (mouse.y / cellSize);
//      applet.stroke(255);
//      applet.rectMode(PConstants.CORNER);
//      applet.noFill();
//      applet.rect(x * cellSize, y * cellSize, cellSize, cellSize);
//    }

    highlightSelected(0, 0xffff0000);
    highlightSelected(1, 0xff00ff00);
    highlightSelected(2, 0xff0000ff);

    applet.stroke(255);
    applet.noFill();
    applet.rect(-1, -1, getWidth() + 2, getHeight() + 2);
  }

  private void drawSelection(Selection selection) {
    applet.stroke(255);
    applet.fill(255, 100);
    applet.rectMode(PConstants.CORNERS);
    applet.rect(selection.x1 * cellSize, selection.y1 * cellSize, (selection.x2 + 1) * cellSize, (selection.y2 + 1) * cellSize);
    applet.rectMode(PConstants.CORNER);  }

  private int cols() {
    return engine.getKernels()[0].length;
  }

  private int rows() {
    return engine.getKernels().length;
  }

  private void highlightSelected(int index, int color) {
    int[][] activeIndexes = engine.getKernelIndex();
    int packIndex = activeIndexes[index][0];
    int kernelIndex = activeIndexes[index][1];
    applet.stroke(color);
    applet.noFill();
    applet.rect(kernelIndex * cellSize, packIndex * cellSize, cellSize - 1, cellSize - 1);
  }

  @Override
  public void mousePressed(PVector mouse) {
    super.mousePressed(mouse);

    int x = (int) (mouse.x / cellSize);
    int y = (int) (mouse.y / cellSize);

    if (selection == null) {
      this.selection = new Selection();
      this.selection.x1 = (int) (mouse.x / cellSize);
      this.selection.y1 = (int) (mouse.y / cellSize);
      this.currentMode = Mode.SELECT;
    } else {
      if (selection.inside(x, y)) {
        currentMode = Mode.DRAG;
      } else {
        this.selection = null;
      }
    }

    lastClickX = x;
    lastClickY = y;
  }

  @Override
  public void mouseReleased(PVector mouse) {
    int x = (int) (mouse.x / cellSize);
    int y = (int) (mouse.y / cellSize);

    switch (currentMode) {
      case SELECT:
        if (this.selection != null) {
          this.selection.x2 = (int) (mouse.x / cellSize);
          this.selection.y2 = (int) (mouse.y / cellSize);
          currentMode = Mode.NONE;
        }
        break;
      case DRAG:
        if (this.selection != null) {
          int dx = x - lastClickX;
          int dy = y - lastClickY;

          if (optionDown) {
            copy(selection, dx, dy);
          } else {
            swap(selection, dx, dy);
          }
          currentMode = Mode.NONE;
          selection = null;
        }
        break;
    }

    super.mouseReleased(mouse);
  }

  @Override
  public float getWidth() {
    return engine.getKernels()[0].length * 7 * scale;
  }

  @Override
  public float getHeight() {
    return engine.getKernels().length * 7 * scale;
  }

  public int getCurrentPack() {
    if (selection == null) {
      return 0;
    }

    return selection.y1;
  }

  public int getCurrentKernel() {
    if (selection == null) {
      return 0;
    }

    return selection.x1;
  }

  @Override
  public void keyPressed(KeyEvent event) {
    super.keyPressed(event);

    if (event.getKeyCode() == 18) { // option
      optionDown = true;
    }

    if (event.getKeyCode() == 8 && mouseInside()) {
      delete(selection);
    }
  }

  @Override
  public void keyReleased(KeyEvent event) {
    super.keyReleased(event);

    if (event.getKeyCode() == 18) { // option
      optionDown = false;
    }

//    if (event.getKeyCode() == 18) { // option
//      currentMode = Mode.SELECT;
//    }
  }

  private void delete(Selection selection) {
    Kernel[][] kernels = engine.getKernels();
    for (int y = selection.y1; y <= selection.y2; y++) {
      for (int x = selection.x1; x <= selection.x2; x++) {
        kernels[y][x].clear();
      }
    }
  }

  private void copy(Selection selection, int copyX, int copyY) {
    Kernel[][] kernels = engine.getKernels();

    Kernel[][] copy = new Kernel[selection.height()][selection.width()];
    for (int y = selection.y1; y <= selection.y2; y++) {
      copy[y - selection.y1] = new Kernel[selection.width()];
      for (int x = selection.x1; x <= selection.x2; x++) {
        copy[y - selection.y1][x - selection.x1] = new Kernel(kernels[y][x]);
      }
    }

    for (int y = selection.y1; y <= selection.y2; y++) {
      for (int x = selection.x1; x <= selection.x2; x++) {
        Kernel c = copy[y - selection.y1][x - selection.x1];
        kernels[y + copyY][x + copyX].set(c);
      }
    }
  }

  private void swap(Selection selection, int dx, int dy) {
    Kernel[][] kernels = engine.getKernels();

    Kernel[][] copy = new Kernel[selection.height()][selection.width()];
    for (int y = selection.y1; y <= selection.y2; y++) {
      copy[y - selection.y1] = new Kernel[selection.width()];
      for (int x = selection.x1; x <= selection.x2; x++) {
        copy[y - selection.y1][x - selection.x1] = new Kernel(kernels[y][x]);
      }
    }

    for (int y = 0; y < selection.height(); y++) {
      for (int x = 0; x < selection.width(); x++) {
        kernels[selection.y1 + y][selection.x1 + x].set(kernels[selection.y1 + y + dy][selection.x1 + x + dx]);
      }
    }

    for (int y = 0; y < selection.height(); y++) {
      for (int x = 0; x < selection.width(); x++) {
        Kernel c = copy[y][x];
        kernels[selection.y1 + y + dy][selection.x1 + x + dx].set(c);
      }
    }
  }
}
