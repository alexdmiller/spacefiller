package influencer;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;

public class MarchingSquares extends InfluencerScene {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new MarchingSquaresGlitch());
  }

  private float cellSize = 20;
  private PVector[][] templates;
  private float threshold = 0.5f;

  @Override
  public void setup() {
    templates = new PVector[16][];

    for (int i = 0; i < 16; i++) {
      templates[i] = new PVector[0];
    }

    // no corners
    templates[0] = new PVector[0];

    // bottom left
    templates[1] = new PVector[] {
        new PVector(0, 0.5f),
        new PVector(0.5f, 1),
    };

    // bottom right
    templates[2] = new PVector[] {
        new PVector(0.5f, 1),
        new PVector(1f, 0.5f),
    };

    // bl, br
    templates[3] = new PVector[] {
        new PVector(0, 0.5f),
        new PVector(1f, 0.5f),
    };

    // tr
    templates[4] = new PVector[] {
        new PVector(0.5f, 0),
        new PVector(1f, 0.5f),
    };
  }

  public float getCellSize() {
    return cellSize;
  }

  public void setCellSize(float cellSize) {
    this.cellSize = cellSize;
  }

  private float f(float x, float y) {
    float dx = x - width / 2;
    float dy = y - height / 2;
    float dist = sqrt(dx * dx + dy * dy);
    return dist / ((sin(frameCount / 20f)+ 1) / 2 * 800) + noise(x / 100f, y/ 100f, frameCount / 50f);
  }

  @Override
  public void draw() {
    background(0);
    stroke(255);
    noStroke();
    for (int x = 0; x < width / cellSize; x++) {
      for (int y = 0; y < height / cellSize; y++) {
        PVector noiseSpace = new PVector(x * cellSize, y * cellSize);

        int tl = f(noiseSpace.x - cellSize/2f, noiseSpace.y - cellSize/2f) > 1 ? 1 : 0;
        int tr = f(noiseSpace.x + cellSize/2f, noiseSpace.y - cellSize/2f) > 1 ? 1 : 0;
        int br = f(noiseSpace.x + cellSize/2f, noiseSpace.y + cellSize/2f) > 1 ? 1 : 0;
        int bl = f(noiseSpace.x - cellSize/2f, noiseSpace.y + cellSize/2f) > 1 ? 1 : 0;

        int index = bl + br * 2 + tr * 4 + tl * 8;

        PVector[] template = templates[index];

        noFill();
        beginShape(LINES);
        stroke(255);
        strokeWeight(3);
        for (PVector p : template) {
          vertex(noiseSpace.x + p.x * cellSize, noiseSpace.y + p.y * cellSize);
          //graphics.point(noiseSpace.x, noiseSpace.y);
        }
        endShape();
      }
    }
  }
}