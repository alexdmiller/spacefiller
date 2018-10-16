package influencer;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
import sketches.Scene;
import spacefiller.particles.Particle;
import spacefiller.particles.ParticleSystem;
import themidibus.MidiBus;
import themidibus.MidiListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.noise.SimplexNoise;
import toxi.processing.ToxiclibsSupport;
import toxi.volume.ArrayIsoSurface;
import toxi.volume.HashIsoSurface;
import toxi.volume.IsoSurface;
import toxi.volume.VolumetricSpaceArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContourBlobs extends Scene implements MidiListener {
  private float fadeSpeed = 0.05f;

  public static void main(String[] args) {
    main("influencer.ContourBlobs");
  }

  MidiBus myBus;
  float[] notes;
  float[] controllers;

  PShader shader;

  static int DIMX=32;
  static int DIMY=32;
  static int DIMZ=32;

  float ISO_THRESHOLD = 0.1f;
  float NS=0.03f;
  Vec3D SCALE=new Vec3D(1,1,1).scaleSelf(1000);

  boolean isWireframe=false;
  float currScale=1;

  VolumetricSpaceArray volume=new VolumetricSpaceArray(SCALE,DIMX,DIMY,DIMZ);
  IsoSurface surface = new HashIsoSurface(volume);

  TriangleMesh mesh1;
  ToxiclibsSupport gfx;
  ExecutorService pool;

  @Override
  protected void doSetup() {
    myBus = new MidiBus(this, "UM-ONE", "UM-ONE");
    notes = new float[127];
    controllers = new float[127];

    shader = loadShader("pixel.glsl", "vert.glsl");
    gfx = new ToxiclibsSupport(this, getCanvas());

    this.pool = Executors.newFixedThreadPool(8);
  }

  public void noteOn(int channel, int pitch, int velocity) {
    println(pitch);
    notes[pitch % notes.length] = 1;
  }

  public void controllerChange(int channel, int controller, int value) {
    println(controller);
    println(value);
    controllers[controller] = value;
  }

  public void noteOff(int channel, int pitch, int velocity) {
//    notes[pitch % notes.length] = false;
  }

  private TriangleMesh computeMesh(Mesh3D mesh) throws InterruptedException {
    List<Callable<Object>> runnables = new ArrayList<>();

    PVector[] particles = new PVector[5];
    for (int i = 0; i < particles.length; i++) {
      particles[i] = new PVector(i * DIMX/5f + DIMX/10, DIMY/2 + cos(frameCount / 200f * 2 * PI + i) * 10, DIMZ/2);
    }

    float[] volumeData=volume.getData();
    for (int z=0; z<DIMZ; z++) {
      runnables.add(Executors.callable(new ProcessCell(z, DIMX, DIMY, DIMZ, volumeData, particles)));//
////          volumeData[index] = x / 4f * y /4f + sin(z / 4f + frameCount / 100f) * 10;
//
//
//
//          index++;

    }

    pool.invokeAll(runnables);


    volume.closeSides();
    // store in IsoSurface and compute surface mesh for the given threshold value

    surface.reset();
    return (TriangleMesh) surface.computeSurfaceMesh(mesh, 0.2f - notes[48] * 0.1f); //notes[48] * 0.1f);
  }


  private void drawMesh(Mesh3D mesh, PGraphics canvas) {
    canvas.beginShape(9);
    Iterator var4;
    Face var5;

    var4 = mesh.getFaces().iterator();

    while(var4.hasNext()) {
      var5 = (Face)var4.next();
      canvas.normal(var5.normal.x, var5.normal.y, var5.normal.z);

      canvas.attrib("height", var5.a.y / SCALE.y + 1f);
      canvas.attrib("width", var5.a.x / SCALE.y + 1f);
      canvas.vertex(var5.a.x, var5.a.y, var5.a.z);

      canvas.attrib("height", var5.b.y / SCALE.y + 1f);
      canvas.attrib("width", var5.b.x / SCALE.y + 1f);
      canvas.vertex(var5.b.x, var5.b.y, var5.b.z);

      canvas.attrib("height", var5.c.y / SCALE.y + 1f);
      canvas.attrib("width", var5.c.x / SCALE.y + 1f);
      canvas.vertex(var5.c.x, var5.c.y, var5.c.z);
    }

    canvas.endShape();
  }

  @Override
  protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {

    for (int i = 0; i < notes.length; i++) {
      if (notes[i] > 0) {
        notes[i] -= fadeSpeed;
      }
    }

    //graphics.camera(0, 0, 500, WIDTH/2, HEIGHT/2, DEPTH/2, 0, -1, 0);
    //graphics.camera(width/2f, height/2f, (height/2f) / tan((float) (PI*30.0 / 180.0)), width/2f, height/2f, 0, 0, 1, 0);
    graphics.ortho();
    graphics.pushMatrix();


    graphics.translate(0, -200, -1000);
    graphics.rotateX(-PI/4);
//    graphics.translate(graphics.width/2,graphics.height/2,0);
    //canvas.rotateX(mouseY*0.01);
    graphics.rotateY(frameCount / 100f);
//    graphics.translate(-graphics.width/2,-graphics.height/2,0);
    graphics.scale(1f);


    try {
      mesh1 = computeMesh(mesh1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

//    graphics.stroke(255);
//    graphics.noFill();
//    graphics.strokeWeight(1);
    graphics.fill(0);
    graphics.noStroke();

    shader.set("spacing", 100f);
    shader.set("dimensions", 0.5f, 0f, 0.5f);

    graphics.shader(shader);
    if (notes[50] > 0) {
      graphics.fill(lerpColor(color(0, 0, 0), color(0, 255, 255), notes[50]));
    } else if (notes[48] > 0) {
      graphics.fill(lerpColor(color(0, 0, 0), color(255, 0, 255), notes[48]));
    } else {
      graphics.fill(0);
    }

//    graphics.fill(color(255, 0, 255));

    drawMesh(mesh1, graphics);

    graphics.noFill();
    graphics.stroke(255);
    graphics.strokeWeight(1);
    graphics.box(SCALE.x, SCALE.y, SCALE.z);

    graphics.popMatrix();

    //camera();
  }


  private class ProcessCell implements Runnable {
    private int z, dimx, dimy, dimz;
    private float[] volumeData;
    private PVector[] particles;

    public ProcessCell(int z, int dimx, int dimy, int dimz, float[] volumeData, PVector[] particles) {
      this.z = z;
      this.dimx = dimx;
      this.dimy = dimy;
      this.dimz = dimz;
      this.volumeData = volumeData;
      this.particles = particles;
    }

    @Override
    public void run() {
      for(int y=0; y<dimy; y++) {
        for (int x = 0; x < dimx; x++) {
          int i = z * (dimx * dimy) + y * (dimx) + x;
//          PVector pos = new PVector(x, y, z);
//          volumeData[i] = 0;
//          for (int j = 0; j < particles.length; j++) {
//            volumeData[i] += 0.1 / pos.dist(particles[j]);
//          }
//          volumeData[i] += (float) (y*y) / dimx / 200f;


          volumeData[i] = (float) ((float) SimplexNoise.noise(x * NS * controllers[48] / 127f, y * NS * controllers[48] / 127f, z * NS / (controllers[48] / 127f + 0.1f), frameCount * NS / 20f) * 0.5);
        }
      }
    }
  }
}
