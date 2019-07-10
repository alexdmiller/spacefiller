package algoplex2.scenes;

import spacefiller.graph.*;
import processing.core.PVector;
import spacefiller.mapping.Quad;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.math.noise.PerlinNoise;

public class GradientTriangleScene extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  @Mod(min = 0, max = 6.2831853f)
  public float color1Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color2Rotation;

  @Mod(min = 0, max = 100)
  public float jitter = 100;

  private PerlinNoise perlin;

  public GradientTriangleScene() {
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(color1Rotation);
    TColor color2 = color1.getRotatedRYB(color2Rotation);

    for (Quad square : grid.getSquares()) {
      float index = 0;

      for (Node[] tri : square.getTriangles()) {
        index += Math.PI / 10;

        TColor c1 = color1.getDarkened((float) (Math.sin(t + index) + 1) / 2);
        TColor c2 = color2.getDarkened((float) (Math.sin(t + Math.PI / 4 + index) + 1) / 2);

        graphics.noStroke();
        graphics.beginShape();
        createVertex(tri[0].position, c1.toARGB(), graphics);
        createVertex(tri[1].position, c1.toARGB(), graphics);
        createVertex(tri[2].position, c2.toARGB(), graphics);
        graphics.endShape();
      }
    }

    super.draw(graphics);
  }

  private void createVertex(PVector p, int color, PGraphics graphics) {
    graphics.fill(color);
    graphics.vertex(
        p.x + (perlin.noise(p.x, p.y, t) - 0.5f) * jitter,
        p.y + (perlin.noise(p.x, p.y, t) - 0.5f) * jitter);
  }

//  @Override
//  public void draw(PGraphics graphics) {
//    t += speed;
//
//    float triangleIndex = 0;
//
//    for (Quad square : grid.getSquares()) {
//      for (Node[] tri : square.getTriangles()) {
//        float vertexSpread = 1;
//        float vertex1Base = triangleIndex + t * 2;
//        float vertex2Base = triangleIndex + t * 3;
//        float vertex3Base = triangleIndex + t * 4;
//
//        graphics.noStroke();
//        graphics.beginShape();
//
//        graphics.fill(
//            (float) (Math.sin(vertex1Base) * 255),
//            (float) (Math.sin(vertex1Base + Math.PI/4 * vertexSpread) * 255),
//            (float) (Math.sin(vertex1Base + Math.PI/2 * vertexSpread) * 255));
//        graphics.vertex(tri[0].position.x, tri[0].position.y);
//        graphics.fill(
//            (float) (Math.sin(vertex2Base + Math.PI/4) * 255),
//            (float) (Math.sin(vertex2Base + Math.PI/4 * vertexSpread) * 255),
//            (float) (Math.sin(vertex2Base + Math.PI/2 * vertexSpread) * 255));
//        graphics.vertex(tri[1].position.x, tri[1].position.y);
//        graphics.fill(
//            (float) (Math.sin(vertex3Base + Math.PI/2) * 255),
//            (float) (Math.sin(vertex3Base + Math.PI/4 * vertexSpread) * 255),
//            (float) (Math.sin(vertex3Base + Math.PI/2 * vertexSpread) * 255));
//        graphics.vertex(tri[2].position.x, tri[2].position.y);
//        graphics.endShape();
//      }
//    }
//
//    super.draw(graphics);
//  }


}
