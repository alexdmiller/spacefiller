package sketches;

import spacefiller.color.SmoothColorTheme;
import de.looksgood.ani.Ani;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.core.PVector;
import spacefiller.ContourSpace;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import spacefiller.remote.VDMXWriter;
import tools.ShapeEditor;
import toxi.color.ColorRange;
import toxi.color.TColor;
import toxi.geom.Quaternion;

import java.util.ArrayList;
import java.util.List;

public class ColorBath extends Scene {
  public static void main(String[] args) {
    main("sketches.ColorBath");
  }

  List<PShape> shapes;

  @Override
  public void doSetup() {
    set2D();

    shapes = new ArrayList<>();

    addSceneTool(new ShapeEditor(shapes, this));

//    OscRemoteControl remote = new OscRemoteControl(this, 12021);
//    VDMXWriter.exportVDMXJson("color-bath", remote.getTargetMap(), remote.getPort());
  }

  @Override
  protected void drawCanvas(PGraphics canvas, float mouseX, float mouseY) {
    for (int i = 0; i < shapes.size(); i++) {
      shapes.get(i).disableStyle();
      canvas.noStroke();
      canvas.beginShape();
      for (int j = 0; j < shapes.get(i).getVertexCount(); j++) {
        PVector v = shapes.get(i).getVertex(j);
        canvas.fill(TColor.BLUE.getRotatedRYB(j * 10).toARGB());
        canvas.vertex(v.x, v.y);
      }
      canvas.endShape();
    }
  }

}
