package influencer;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import sketches.Scene;

public class MarchingSquaresGlitch extends Scene {
  private float cellSize = 10;
  private PVector[][] templates;
  private float threshold = 0.5f;

  public static void main(String[] args) {
    main("influencer.MarchingSquaresGlitch");
  }

  @Override
  protected void doSetup() {
    set2D();

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

  private float f(float x, float y) {
    return noise(x / 100f, y/ 10f, frameCount / 50f) * 2;
  }

  @Override
  protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
    graphics.stroke(255);

//    graphics.beginShape(LINES);
//    graphics.vertex(30, 20);
//    graphics.vertex(85, 20);
//    graphics.vertex(85, 75);
//    graphics.vertex(30, 75);
//    graphics.endShape();


    graphics.translate(-WIDTH/2, -HEIGHT/2);
    graphics.noStroke();
    for (int x = 0; x < WIDTH / cellSize; x++) {
      for (int y = 0; y < HEIGHT / cellSize; y++) {
        PVector noiseSpace = new PVector(x * cellSize, y * cellSize);

        int tl = (int) f(noiseSpace.x - cellSize/2f, noiseSpace.y - cellSize/2f);
        int tr = (int) f(noiseSpace.x + cellSize/2f, noiseSpace.y - cellSize/2f);
        int br = (int) f(noiseSpace.x + cellSize/2f, noiseSpace.y + cellSize/2f);
        int bl = (int) f(noiseSpace.x - cellSize/2f, noiseSpace.y + cellSize/2f);

        int index = tl + tr * 2 + br * 4 + bl * 8;

        PVector[] template = templates[index];


        graphics.noFill();
        graphics.beginShape(LINES);
        graphics.stroke(255);
        graphics.strokeWeight(2);
        for (PVector p : template) {
          graphics.vertex(noiseSpace.x + p.x * cellSize, noiseSpace.y + p.y * cellSize);
          //graphics.point(noiseSpace.x, noiseSpace.y);
        }
        graphics.endShape();
      }
    }
  }
}