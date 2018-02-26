package sketches;

import codeanticode.syphon.SyphonServer;
import processing.core.PApplet;
import processing.opengl.PJOGL;
import processing.opengl.PShader;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import processing.core.PGraphics;
import processing.core.PVector;
import spacefiller.remote.VDMXWriter;
import toxi.sim.automata.CAMatrix;
import common.Circle;
import toxi.sim.automata.CARule;
import toxi.sim.automata.CARule2D;

public class Blobs extends PApplet {
  public static void main(String[] args) {
    main("sketches.Blobs");
  }

  PShader shader;
  PGraphics shaderCanvas;
  SyphonServer server;

  int lifeCellSize = 50;

  @Mod(min = 1, max = 50, defaultValue = 1)
  public float pixelate = 1;

  @Mod(min = 0, max = 3, defaultValue = 1)
  public float k = 1;

  @Mod(min = 1, max = 40, defaultValue = 1)
  public float circleRadius = 20;

  public int textureDivisor = 3;

  CAMatrix ca;
  int cols, rows;

  float transition = 0;
  Circle[] circles;

  @Mod(min = 0.5f, max = 3)
  public float circleGrowthSpeed = 1;

  float simulationStepSpeed = 0.1f;

  public void settings() {
    size(1920, 1080, P2D);
    PJOGL.profile = 1;
  }

  public void setup() {
    server = new SyphonServer(this, "Blobs");

    OscRemoteControl remote = new OscRemoteControl(this, 12011);
    VDMXWriter.exportVDMXJson("blobs", remote.getTargetMap(), remote.getPort());

    shader = loadShader("frag.glsl");

    shaderCanvas = createGraphics(floor(width / textureDivisor), floor(height / textureDivisor), P3D);

    shaderCanvas.beginDraw();
    shaderCanvas.background(0);
    shaderCanvas.endDraw();

    cols = width / lifeCellSize;
    rows = height / lifeCellSize;

    ca = new CAMatrix(cols, rows);
    circles = new Circle[cols * rows];

    byte[] birthRules = new byte[] { 3 };
    // survival rules specify the possible numbers of required
    // ACTIVE neighbour cells in order for a cell to stay alive
    byte[] survivalRules = new byte[] { 2,3 };
    CARule rule = new CARule2D(birthRules,survivalRules,2,true);
    // assign the rules to the CAMatrix
    ca.setRule(rule);
    ca.addNoise(0.5f, 0, 100);

    stepSimulation();
  }

  @Mod
  public void stepSimulation() {
    ca.update();
  }

  @Mod
  public void addNoise() {
    ca.addNoise(0.5f, 0, 100);
  }

  @Mod
  public void clear() {
    ca.reset();
  }


  public void draw() {
    background(0);

    stroke(255);
    noFill();

    int[] grid = ca.getMatrix();
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (grid[row * cols + col] == 1) {
          if (circles[row * cols + col] != null) {
            if (circles[row * cols + col].radius < circleRadius) {
              circles[row * cols + col].radius += circleGrowthSpeed;
            }
          } else {
            circles[row * cols + col] = new Circle(col * lifeCellSize, row * lifeCellSize, 1);
          }
        } else {
          if (circles[row * cols + col] != null) {
            if (circles[row * cols + col].radius > 0) {
              circles[row * cols + col].radius -= circleGrowthSpeed;
            } else {
              circles[row * cols + col] = null;
            }
          }
        }
      }
    }

    transition += simulationStepSpeed;

//    if (transition >= 1) {
//      stepSimulation();
//      transition = 0;
//    }

    int nextDataIndex = 0;
    float[] data = new float[1000 * 3];
    for (int i = 0; i < circles.length; i++) {
      if (circles[i] != null && nextDataIndex < 1000) {
        // circles[i].update();
        data[nextDataIndex * 3] = circles[i].position.x / textureDivisor;
        data[nextDataIndex * 3 + 1] = height / textureDivisor - circles[i].position.y / textureDivisor;
        data[nextDataIndex * 3 + 2] = circles[i].radius / textureDivisor;
        nextDataIndex++;
      }
    }

    shader.set("metaballs", data, 3);
    shader.set("pixelate", round(pixelate));
    shader.set("k", k);
    shaderCanvas.beginDraw();
    shaderCanvas.background(0);
    shaderCanvas.shader(shader);
    shaderCanvas.beginShape(QUAD);
    shaderCanvas.textureMode(NORMAL);
    shaderCanvas.textureWrap(REPEAT);
    shaderCanvas.vertex(0, 0, 0, 0);
    shaderCanvas.vertex(width / textureDivisor, 0, 1, 0);
    shaderCanvas.vertex(width / textureDivisor, height / textureDivisor, 1, 1);
    shaderCanvas.vertex(0, height / textureDivisor, 0, 1);
    shaderCanvas.endShape();
    shaderCanvas.endDraw();

    // image(shaderCanvas, 0,0, width, height);
    server.sendImage(shaderCanvas);
  }
}
