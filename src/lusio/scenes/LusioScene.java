package lusio.scenes;

import graph.Graph;
import lightcube.Lightcube;
import scene.Scene;

import java.util.Map;

/**
 * Created by miller on 8/19/17.
 */
public class LusioScene extends Scene {
  protected Map<String, Graph> graphs;
  protected Lightcube cube;

  public void setGraphs(Map<String, Graph> graphs) {
    this.graphs = graphs;
  }

  public void setCube(Lightcube cube) {
    this.cube = cube;
  }
}
