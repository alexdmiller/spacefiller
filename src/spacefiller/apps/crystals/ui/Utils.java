package spacefiller.apps.crystals.ui;

import spacefiller.crystals.engine.Animator;
import processing.core.PGraphics;
import processing.core.PImage;

public class Utils {
  public static PImage[] makePreviews(PGraphics canvas, Animator[] animators) {
    PImage[] previews = new PImage[animators.length];

    canvas.beginDraw();
    canvas.background(0);
    canvas.endDraw();

    for (int i = 0; i < animators.length; i++) {
      canvas.beginDraw();
      canvas.background(0);
      animators[i].preview(canvas);
      canvas.endDraw();

      previews[i] = canvas.get();
    }

    return previews;
  }

}
