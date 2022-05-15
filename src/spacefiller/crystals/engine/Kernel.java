package spacefiller.crystals.engine;

import processing.core.*;

import java.io.Serializable;
import java.util.Arrays;

import static javax.swing.SwingConstants.CENTER;
import static processing.core.PConstants.ALPHA;
import static processing.core.PConstants.P2D;

public class Kernel implements Serializable {
  public static float MAX_THRESHOLD = 10;

  private transient PImage rendered;

  private float[] matrix;
  private float[] thresholds;
  private int size;
  private PApplet parent;
  private float cellRange = 20;
  private int frameskips = 1;

  public Kernel(int size, PApplet parent) {
    this.size = size;
    this.matrix = new float[size * size];
    this.thresholds = new float[3];
    this.parent = parent;
    this.rendered = parent.createImage(size, size, PConstants.RGB);
  }

  public Kernel(Kernel other) {
    this.matrix = new float[size * size];
    this.thresholds = new float[3];
    this.set(other);
  }

  public void clear() {
    this.matrix = new float[size * size];
    this.thresholds = new float[3];
    this.frameskips = 1;
  }

  public void setMatrix(float[] matrix) {
    this.matrix = matrix;
  }

  public void setThresholds(float[] thresholds) {
    this.thresholds = thresholds;
  }

  public void setThresholds(float r, float g, float b) {
    this.thresholds[0] = r;
    this.thresholds[1] = g;
    this.thresholds[2] = b;
  }

  public void setFrameskips(int frameskips) {
    this.frameskips = frameskips;
  }

  public int getFrameskips() {
    return frameskips;
  }

  public void addToThreshold(int index, float adjustment) {
    thresholds[index] =
        Math.max(
            Math.min(
                thresholds[index] + adjustment,
                MAX_THRESHOLD),
            0);
  }

  public float getCell(int x, int y) {
    return matrix[y * size + x];
  }

  public void setCell(int x, int y, float value) {
    matrix[y * size + x] = value;
  }

  public void set(Kernel kernel) {
    this.matrix = new float[kernel.matrix.length];
    for (int i = 0; i < kernel.matrix.length; i++) {
      this.matrix[i] = kernel.matrix[i];
    }

    this.thresholds = new float[3];
    for (int i = 0; i < kernel.thresholds.length; i++) {
      this.thresholds[i] = kernel.thresholds[i];
    }

    this.size = kernel.size;
    this.cellRange = kernel.cellRange;
    this.frameskips = kernel.frameskips;
  }

  public void addToCell(int x, int y, float adjustment) {
    matrix[y * size + x] =
        Math.max(
            Math.min(
                matrix[y * size + x] + adjustment,
                cellRange / 2),
        -cellRange / 2);
  }

  public int getSize() {
    return size;
  }

  public PImage getRendered() {
    rendered.loadPixels();
    for (int x = 0; x < getSize(); x++) {
      for (int y = 0; y < getSize(); y++) {
        rendered.pixels[y * getSize() + x] = parent.color((getCell(x, y) / cellRange + 0.5f) * 255);
      }
    }
    rendered.updatePixels();

    return rendered;
  }

  public float[] getThresholds() {
    return thresholds;
  }

  public String toString() {
    return Arrays.toString(matrix);
  }
}