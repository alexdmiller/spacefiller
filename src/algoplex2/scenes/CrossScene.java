package algoplex2.scenes;

import algoplex2.Algoplex2;
import algoplex2.Quad;
import graph.*;
import spacefiller.remote.Mod;
import processing.core.PGraphics;
import toxi.color.ColorList;
import toxi.color.ColorRange;
import toxi.color.ReadonlyTColor;
import toxi.color.TColor;
import toxi.color.theory.ColorTheoryRegistry;
import toxi.color.theory.ColorTheoryStrategy;
import toxi.math.noise.PerlinNoise;

import static processing.core.PConstants.P3D;

public class CrossScene extends GridScene {
  @Mod(min = 0, max = 1)
  public float crossSize = 0.5f;

  @Mod(min = 0, max = 1)
  public float xSize = 0.5f;

  @Mod(min = 0, max = 6.2831853f)
  public float color1Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color2Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color3Rotation;

  @Mod(min = 0, max = 6.2831853f)
  public float color4Rotation;

  @Mod(min = 0, max = (float) Math.PI)
  public float rotation;

  private PGraphics crosses;

  @Override
  public void setup() {
    crosses = Algoplex2.instance.createGraphics((int) grid.getWidth(), (int) grid.getHeight());
  }

  @Override
  public void draw(PGraphics graphics) {
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

    crosses.beginDraw();
    crosses.clear();
    crosses.strokeWeight(5);
    crosses.stroke(255);
    for (Quad square : grid.getSquares()) {
      if (square.getTopLeft().position.x != 0 && square.getTopLeft().position.y != 0) {
        float crossSize = this.crossSize * grid.getCellSize();
        crosses.pushMatrix();
        crosses.translate(square.getTopLeft().position.x, square.getTopLeft().position.y);
        //crosses.rotate(rotation);
        crosses.line(-crossSize, 0, crossSize, 0);
        crosses.line(0, -crossSize, 0, crossSize);
        crosses.popMatrix();
      }

      float xSize = this.xSize * grid.getCellSize() / 2;
      crosses.pushMatrix();
      crosses.translate(square.getCenter().position.x, square.getCenter().position.y);
      crosses.rotate(rotation);
      crosses.line(-xSize, -xSize, xSize, xSize);
      crosses.line(-xSize, xSize, xSize, -xSize);
      crosses.popMatrix();
    }
    crosses.endDraw();

    graphics.mask(crosses);
    // graphics.image(crosses, 0, 0);

    super.draw(graphics);
  }


}
