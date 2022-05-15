package spacefiller.crystals.engine;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

import static processing.core.PConstants.DISABLE_TEXTURE_MIPMAPS;

public class Renderer {
  private PGraphics simulationLayer;
  private PGraphics fxLayer;
  private PShader shader;
  private PShader fxShader;
  private Kernel[] kernels;
  private PImage kernelMap;
  private PImage seed;
  private boolean renderMap;
  private boolean clearFlag;
  private float timeBlur;

  public Renderer(PApplet applet, int width, int height) {
    simulationLayer = applet.createGraphics(width, height, PConstants.P3D);
    simulationLayer.noSmooth();
    simulationLayer.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)simulationLayer).textureSampling(3);

    fxLayer = applet.createGraphics(width, height, PConstants.P3D);
    fxLayer.noSmooth();
    fxLayer.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL) fxLayer).textureSampling(3);

    shader = applet.loadShader("data/shaders/core.glsl");
    shader.set("resolution", (float) width, (float) height);

    fxShader = applet.loadShader("data/shaders/fx.glsl");
    fxShader.set("resolution", (float) width, (float) height);

    kernels = new Kernel[] {
        new Kernel(7, applet),
        new Kernel(7, applet),
        new Kernel(7, applet)
    };
  }

  public Kernel[] getKernels() {
    return kernels;
  }

  public void setKernels(Kernel k1, Kernel k2, Kernel k3) {
    this.kernels = new Kernel[] { k1, k2, k3 };
  }

  public void setKernel(int i, Kernel k) {
    this.kernels[i] = k;
  }

  public void setKernels(Kernel[] kernels) {
    this.kernels = kernels;
  }

  public PImage getKernelMap() {
    return kernelMap;
  }

  public void setKernelMap(PImage kernelMap) {
    this.kernelMap = kernelMap;
  }

  public PImage getSeed() {
    return seed;
  }

  public void setSeed(PImage seed) {
    this.seed = seed;
  }


  public PImage render(int updates) {
    shader.set("kernelMap", kernelMap);
    shader.set("seedLayer", seed);

    shader.set("kernelSize", kernels[0].getSize());
    shader.set("kernel1", kernels[0].getRendered());
    shader.set("kernel2", kernels[1].getRendered());
    shader.set("kernel3", kernels[2].getRendered());

    float[] thresholds1 = kernels[0].getThresholds();
    float[] thresholds2 = kernels[1].getThresholds();
    float[] thresholds3 = kernels[2].getThresholds();

    shader.set("threshold1", thresholds1[0], thresholds1[1], thresholds1[2]);
    shader.set("threshold2", thresholds2[0], thresholds2[1], thresholds2[2]);
    shader.set("threshold3", thresholds3[0], thresholds3[1], thresholds3[2]);
    shader.set("renderMap", renderMap);

    for (int i = 0; i < updates; i++) {
      simulationLayer.beginDraw();
      simulationLayer.background(0);
      if (!clearFlag) {
        simulationLayer.shader(shader);
        simulationLayer.fill(255, 0, 0);
        simulationLayer.rect(0, 0, simulationLayer.width, simulationLayer.height);
      }
      clearFlag = false;
      simulationLayer.endDraw();
    }

    fxShader.set("inputLayer", simulationLayer);
    fxShader.set("timeBlur", timeBlur);

    fxLayer.beginDraw();
    fxLayer.background(0);
    fxLayer.shader(fxShader);
    fxLayer.fill(0, 255, 0);
    fxLayer.rect(0, 0, simulationLayer.width, simulationLayer.height);
    fxLayer.endDraw();

    return fxLayer;
  }

  public int getSimWidth() {
    return simulationLayer.width;
  }

  public int getSimHeight() {
    return simulationLayer.height;
  }

  public void setRenderMap(boolean renderMap) {
    this.renderMap = renderMap;
  }

  public boolean getRenderMap() {
    return renderMap;
  }

  public void clear() {
    clearFlag = true;
  }

  public PGraphics getSimulation() {
    return simulationLayer;
  }

  public float getTimeBlur() {
    return timeBlur;
  }

  public void setTimeBlur(float timeBlur) {
    this.timeBlur = timeBlur;
  }
}
