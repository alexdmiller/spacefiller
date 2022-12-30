package spacefiller.spaceplants.rendering;

import processing.core.PImage;
import spacefiller.spaceplants.SPSystem;

public interface Renderer {
  void render(Iterable<SPSystem> systems);
}
