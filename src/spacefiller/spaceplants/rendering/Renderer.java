package spacefiller.spaceplants.rendering;

import spacefiller.spaceplants.SPSystem;

public interface Renderer {
  void render(Iterable<SPSystem> systems, int frames);
}
