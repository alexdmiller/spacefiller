package sketches;

import spacefiller.color.SmoothColorTheme;
import de.looksgood.ani.Ani;
import de.looksgood.ani.AniSequence;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.ContourSpace;
import spacefiller.LineSegment;
import spacefiller.Vector;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import spacefiller.remote.VDMXWriter;
import toxi.color.ColorRange;
import toxi.geom.Quaternion;
import toxi.math.ExponentialInterpolation;
import toxi.math.InterpolateStrategy;

import java.util.ArrayList;
import java.util.List;

public class Contours2018 extends Scene {
  @Mod(min = 0.2f, max = 3)
  public float maxThreshold = 2;

  @Mod(min = 10, max = 100)
  public float maxRadius = 30;

  @Mod(min = 0, max = 1f)
  public float noise = 0;

  @Mod(min = 0.1f, max = 0.5f)
  public float noiseScale = 1;

  @Mod(min = 0, max = 0.05f)
  public float noiseSpeed = 0;

  @Mod(min = 0, max = 1)
  public float colorSpread = 0;

  @Mod(min = 0, max = 0.2f)
  public float colorSpeed = 0f;

  @Mod(min = 1, max = 10 )
  public float lineThickness = 3;

  @Mod(min = 0, max = 20)
  public float layerHeight = 1;

  public static void main(String[] args) {
    main("sketches.Contours2018");
  }

  private ContourSpace contourSpace;
  private List<Bubble> bubbles;
  private SmoothColorTheme colors;
  private float noisePosition = 0;
  private float colorPosition = 0;
  private Quaternion orientation = new Quaternion();
  private Quaternion targetOrientation = new Quaternion();
  private float interpolationAmount = 0;
  private InterpolateStrategy interpolateStrategy = new ExponentialInterpolation(0.8f);

  @Override
  public void doSetup() {
    Ani.init(this);

    set3D();

    contourSpace = new ContourSpace(WIDTH + 50, HEIGHT + 50, 50);

//    OscRemoteControl remote = new OscRemoteControl(this, 12020);
//    VDMXWriter.exportVDMXJson("contours-2018", remote.getTargetMap(), remote.getPort());

    bubbles = new ArrayList<>();

    for (int i = 0; i < 50; i++) {
      bubbles.add(new Bubble((float) Math.random() * WIDTH, (float) Math.random() * HEIGHT));
    }

    colors = new SmoothColorTheme(ColorRange.FRESH, 10, 100);
  }

  @Mod
  public void newColors() {
    colors = new SmoothColorTheme(ColorRange.FRESH, 10, 100);
  }

  @Mod
  public void grow() {
    for (Bubble bubble : bubbles) {
      bubble.grow();
    }
  }

  @Mod
  public void shrink() {
    for (Bubble bubble : bubbles) {
      bubble.shrink();
    }
  }

  @Mod
  public void move() {
    for (Bubble bubble : bubbles) {
      bubble.move();
    }
  }

  @Override
  public void doMouseDown(float x, float y) {
//    bubbles.add(new Bubble(x + WIDTH / 2, y + HEIGHT / 2));
  }

  @Mod
  public void mutate() {
    for (Bubble b : bubbles) {
      if (Math.random() < 0.5) {
        b.grow();
      } else {
        b.shrink();
      }
    }
  }

  @Override
  protected void drawCanvas(PGraphics canvas, float mouseX, float mouseY) {
    Quaternion current = orientation;

    if (!orientation.equals(targetOrientation)) {
      if (interpolationAmount < 1) {
        current = orientation.interpolateTo(targetOrientation, interpolationAmount, interpolateStrategy);
      }

      if (interpolationAmount >= 1) {
        interpolationAmount = 0;
        orientation = targetOrientation;
        current = orientation;
      }
    }

    float[] axis = current.toAxisAngle();
    canvas.rotate(axis[0], axis[1], axis[2], axis[3]);

    noisePosition += noiseSpeed;
    colorPosition += colorSpeed;

    contourSpace.resetGrid();
    contourSpace.clearLineSegments();

    for (Bubble b : bubbles) {
      contourSpace.addMetaBall(new Vector(b.position.x, b.position.y), b.radius * maxRadius, 1);
    }

    contourSpace.addNoise(noiseScale, noise, noisePosition);

    float step = 0.02f;
    float threshold = 0.2f;
    while (threshold < maxThreshold) {
      contourSpace.drawIsoContour(threshold);
      threshold += step;
      step *= 1.1;
    }

    canvas.pushMatrix();

    canvas.translate(-WIDTH / 2, -HEIGHT / 2);
    canvas.strokeWeight(lineThickness);
    canvas.stroke(255);
    float colorIndex = colorPosition;
    for (List<LineSegment> layer : contourSpace.getLayers()) {
      canvas.translate(0, 0, layerHeight);
      canvas.stroke(colors.getColor(colorIndex).toARGB());
      colorIndex += colorSpread;
      for (LineSegment segment : layer) {
        canvas.line(segment.p1.x, segment.p1.y, segment.p2.x, segment.p2.y);
      }
    }

    canvas.popMatrix();
  }

  @Mod
  public void resetOrientation() {
    orientation = targetOrientation;
    targetOrientation = Quaternion.createFromEuler(0, 0, 0);
    interpolationAmount = 0;
    Ani.to(this, 1, "interpolationAmount", 1);
  }

  @Mod
  public void newOrientation() {
    orientation = targetOrientation;
    targetOrientation = Quaternion.createFromEuler(
        0,
        floor(random(4)) / 4f * PI * 2,
        floor(random(4)) / 4f * PI * 2 + PI / 4);

    interpolationAmount = 0;
    Ani.to(this, 1, "interpolationAmount", 1);
  }

  @Override
  protected void drawControlPanel(PGraphics graphics, float mouseX, float mouseY) {

  }

  private class Bubble {
    PVector position;
    float radius;

    public Bubble(float x, float y) {
      position = new PVector(x, y);
      radius = 0;
    }

    public void grow() {
      Ani.to(this, 2, "radius", 1);
    }

    public void shrink() {
      Ani.to(this, 1, "radius", 0);
    }

    public void move() {
      Ani.to(position, 2, "x", (float) Math.random() * WIDTH);
      Ani.to(position, 2, "y", (float) Math.random() * HEIGHT);
    }
  }
}
