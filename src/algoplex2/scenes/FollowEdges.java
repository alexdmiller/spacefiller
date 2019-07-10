package algoplex2.scenes;

import spacefiller.graph.GraphWobbler;
import spacefiller.graph.renderer.*;
import processing.core.PGraphics;
import spacefiller.mapping.Grid;
import spacefiller.remote.Mod;

/**
 * Created by miller on 10/2/17.
 */
public class FollowEdges extends GridScene {
  @Mod
  public AnimatedFillGraphRenderer animatedFillGraphRenderer;

  @Mod
  public SinGraphRenderer sinGraphRenderer;

  @Mod
  public DottedLineGraphRenderer dottedLineGraphRenderer;

  @Mod
  public GraphWobbler wobbler;

  @Mod(min = 0, max = 2)
  public float rendererIndex;

  @Override
  public void preSetup(Grid grid) {
    super.preSetup(grid);

    wobbler = new GraphWobbler(grid);
    sinGraphRenderer = new SinGraphRenderer();
    dottedLineGraphRenderer = new DottedLineGraphRenderer();
  }

  @Override
  public void draw(PGraphics graphics) {
    wobbler.update();

    if (rendererIndex < 1) {
      sinGraphRenderer.render(graphics, wobbler);
    } else {
      dottedLineGraphRenderer.render(graphics, wobbler);
    }

    super.draw(graphics);
  }
}
