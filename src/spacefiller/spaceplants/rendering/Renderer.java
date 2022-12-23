package spacefiller.spaceplants.rendering;

import processing.core.PImage;
import spacefiller.spaceplants.SPSystem;

public interface Renderer {
  PImage render(Iterable<SPSystem> systems);
}
