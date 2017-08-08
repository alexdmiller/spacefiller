package particles.renderers;

import megamu.mesh.Delaunay;
import processing.core.PGraphics;

public class DelaunayRenderer extends ParticleRenderer {
  private float lineSize;

  public DelaunayRenderer(float lineSize) {
    this.lineSize = lineSize;
  }

  @Override
  public void render(PGraphics graphics) {
    graphics.strokeWeight(lineSize);
    float[][] points = new float[particles.size()][2];
    for (int i = 0; i < particles.size(); i++) {
      points[i][0] = particles.get(i).position.x;
      points[i][1] = particles.get(i).position.y;
    }
    Delaunay delaunay = new Delaunay(points);
    float[][] myEdges = delaunay.getEdges();
    for(int i=0; i<myEdges.length; i++) {
      float startX = myEdges[i][0];
      float startY = myEdges[i][1];
      float endX = myEdges[i][2];
      float endY = myEdges[i][3];
      graphics.line( startX, startY, endX, endY );
    }
  }
}
