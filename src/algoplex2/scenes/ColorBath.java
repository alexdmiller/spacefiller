package algoplex2.scenes;

import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.math.noise.PerlinNoise;

public class ColorBath extends GridScene {
  private float t = 0;

  @Mod(min = -0.1f, max = 0.1f)
  public float speed = 0.01f;

  @Mod(min = 0, max = 6.2831853f)
  public float color1Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color2Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color3Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color4Rotation;

  private PerlinNoise perlin;

  public ColorBath() {
    perlin = new PerlinNoise();
  }

  @Override
  public void draw(PGraphics graphics) {
    t += speed;

    ReadonlyTColor color1 = TColor.BLUE.getRotatedRYB(color1Rotation);
    TColor color2 = color1.getRotatedRYB(color2Rotation);
    TColor color3 = color1.getRotatedRYB(color3Rotation);
    TColor color4 = color1.getRotatedRYB(color4Rotation);

    graphics.beginShape();
    graphics.fill(color1.toARGB());
    graphics.vertex(grid.getBoundingQuad().getTopLeft().position.x, grid.getBoundingQuad().getTopLeft().position.y);
    graphics.fill(color2.toARGB());
    graphics.vertex(grid.getBoundingQuad().getTopRight().position.x, grid.getBoundingQuad().getTopRight().position.y);
    graphics.fill(color3.toARGB());
    graphics.vertex(grid.getBoundingQuad().getBottomRight().position.x, grid.getBoundingQuad().getBottomRight().position.y);
    graphics.fill(color4.toARGB());
    graphics.vertex(grid.getBoundingQuad().getBottomLeft().position.x, grid.getBoundingQuad().getBottomLeft().position.y);
    graphics.endShape();

    super.draw(graphics);
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
