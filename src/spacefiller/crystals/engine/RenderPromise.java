package spacefiller.crystals.engine;

import processing.core.PImage;

public class RenderPromise {
  public PImage image;
  public ResolveCallback onResolve;

  public RenderPromise(PImage image) {
    this.image = image;
  }

  public RenderPromise() {
  }

  public static interface ResolveCallback {
    void resolve();
  }
}
