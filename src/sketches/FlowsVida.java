package sketches;

import codeanticode.syphon.SyphonServer;
import spacefiller.color.SmoothColorTheme;
import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.remote.VDMXWriter;
import toxi.color.*;

import static java.lang.Math.PI;

public class FlowsVida extends PApplet {
  public static void main(String[] args) {
    main("sketches.FlowsVida");
  }

  @Mod(min = 0, max = 20)
  public float flowForce = 5;

  @Mod(min = 200, max = 1000)
  public float noiseScale = 100;

  @Mod(min = -0.05f, max = 0.05f)
  public float noiseSpeed1 = 0.0f;

  @Mod(min = -0.05f, max = 0.05f)
  public float noiseSpeed2 = 0.003f;

  @Mod(min = 10, max = 50)
  public float lineLength = 10;

  @Mod(min = 0, max = 100)
  public float scrollSpeed = 1;

  @Mod(min = 0, max = 20)
  public float fallSpeed = 10;

  @Mod(min = -1, max = 1)
  public float lineSparsity = 1.0f;

  @Mod(min = 1, max = 10)
  public float lineThickness = 2.0f;

  @Mod(min = 0, max = 1)
  public float interpolation = 0f;

  @Mod(min = 0, max = 500)
  public float circleRadius = 100;

  @Mod(min = 1, max = 1000)
  public int numPoints = 50;

  @Mod(min = 0, max = 0.01f)
  public float scrambleSpeed = 0.01f;

  @Mod(min = 1, max = 20)
  public float colorSpread = 4;

  @Mod(min = 0, max = 6.283185307179586f)
  public float colorStart = 0;

  @Mod(min = 0, max = 0.1f)
  public float colorSpeed = 0.01f;

  float timeStep;
  float scramble = 0;
  float noise1Pos = 0;
  float noise2Pos = 0;
  private float colorPos = 0;

  SyphonServer server;

  SmoothColorTheme colors;

  PGraphics simulation;
  PShader pixelate;

  public void settings() {
    size(1000, 300, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    colors = new SmoothColorTheme(ColorRange.FRESH, 10, 100);

    simulation = createGraphics(width / 2, height / 2, P2D);
    pixelate = loadShader("pixelate.glsl");

    pixelate.set("pixelSize", simulation.width / 2.0F, simulation.height / 2.0F);
    pixelate.set("pixelOffset", 0.5F / simulation.width, 0.5F / simulation.height);

    noSmooth();
    simulation.noSmooth();

    server = new SyphonServer(this, "flows");
  }

  @Mod
  public void switchColors() {
    colors = new SmoothColorTheme(ColorRange.BRIGHT, 10, 100);
  }

  public void draw() {
    PGraphics graphics = simulation;

    graphics.beginDraw();
    graphics.noSmooth();
    ((PGraphicsOpenGL) g).textureSampling(POINT);
    ((PGraphicsOpenGL) graphics).textureSampling(POINT);


    timeStep += 0.01;
    scramble += scrambleSpeed;
    noise1Pos += noiseSpeed1;
    noise2Pos += noiseSpeed2;
    colorPos += colorSpeed;

    graphics.background(0);
    graphics.stroke(255);
    graphics.strokeWeight(lineThickness);

    for (int j = -50; j <= simulation.width + 50; j += 10) {
      PVector p = new PVector(j, simulation.height);
//      PVector.add(
//          PVector.mult(position3(j), interpolation),
//          PVector.mult(position2(j), (1 - interpolation)));

      for (int i = 0; i < lineLength; i++) {

        float oldX = p.x;
        float oldY = p.y;
        PVector v = getFlow(p.x, p.y);

        p.x += v.x;
        p.y += v.y - fallSpeed;

        if (Math.sin(i + (noise((float) j) * 100.0) + timeStep * scrollSpeed) - lineSparsity < 0) {
          graphics.stroke(colors.getColor(i / colorSpread + colorStart + colorPos).toARGB());
          graphics.line(oldX, oldY, p.x, p.y);
        }
      }
    }

    graphics.endDraw();

    shader(pixelate);
    image(graphics, 0, 0, width, height);
    server.sendScreen();
  }

  PVector getFlow(float x, float y) {
    float angle = noise(x / noiseScale, y / noiseScale - noise1Pos, noise2Pos) * PI * 6;
    return PVector.fromAngle(angle).setMag(flowForce);
  }

  PVector position1(int i) {
    return new PVector(((float) simulation.width / numPoints) * i - simulation.width / 2, -simulation.height / 2);
  }

  PVector position2(int i) {
    float theta = 2 * PI * (float) i / numPoints + timeStep;
    PVector p = new PVector(
        cos(theta) * circleRadius,
        sin(theta) * circleRadius
    );
    return p;
  }

  PVector position3(int i) {
    int cellSize = 100;
    int cols = width / cellSize;
    float x = i % cols * cellSize - width / 2;
    float y = i / cols * cellSize - height / 2;
    PVector p = new PVector(x, y);

    return p;
  }
}
