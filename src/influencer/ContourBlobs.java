package influencer;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;
import spacefiller.remote.signal.FloatNode;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.Mesh3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.math.noise.SimplexNoise;
import toxi.processing.ToxiclibsSupport;
import toxi.volume.HashIsoSurface;
import toxi.volume.IsoSurface;
import toxi.volume.VolumetricSpaceArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContourBlobs extends InfluencerScene {
  public static void main(String[] args) {
    SceneHost.getInstance().start(new ContourBlobs());
  }

  private FloatNode noiseSize = new FloatNode(); //control.controller(14).scale(1f, 3f).smooth(0.02f).toFloat();
  private FloatNode morph = new FloatNode(); //mainSlider.smooth(0.1f).toFloat();
  private FloatNode smoothKick = new FloatNode();//kickDecay.smooth(0.5f).toFloat();
  private FloatNode orientation = new FloatNode();//control.controller(15).smooth(0.05f).toFloat();
  private FloatNode rotationSpeed = new FloatNode();//control.controller(16).scale(0, 0.3f).smooth(0.1f).toFloat();

  private PShader shader;
  private float rotation;

  static int DIMX=32;
  static int DIMY=32;
  static int DIMZ=32;

  float ISO_THRESHOLD = 0.1f;
  float NS=0.03f;
  Vec3D SCALE=new Vec3D(1,1,1).scaleSelf(1000);

  VolumetricSpaceArray volume=new VolumetricSpaceArray(SCALE,DIMX,DIMY,DIMZ);
  IsoSurface surface = new HashIsoSurface(volume);

  TriangleMesh mesh1;
  ToxiclibsSupport gfx;
  ExecutorService pool;

  @Override
  public void setup() {
    noiseSize.setValue(1f);

    shader = loadShader("pixel.glsl", "vert.glsl");
    gfx = new ToxiclibsSupport(this, getGraphics());

    this.pool = Executors.newFixedThreadPool(8);
  }

  private TriangleMesh computeMesh(Mesh3D mesh) throws InterruptedException {
    List<Callable<Object>> runnables = new ArrayList<>();

    PVector[] particles = new PVector[5];
    for (int i = 0; i < particles.length; i++) {
      particles[i] = new PVector(i * DIMX/5f + DIMX/10, DIMY/2 + cos(frameCount / 200f * 2 * PI + i) * 10, DIMZ/2);
    }

    float[] volumeData=volume.getData();
    for (int z=0; z<DIMZ; z++) {
      runnables.add(Executors.callable(new ProcessCell(z, DIMX, DIMY, DIMZ, volumeData, particles)));
    }

    pool.invokeAll(runnables);


    volume.closeSides();
    // store in IsoSurface and compute surface mesh for the given threshold value

    surface.reset();
    return (TriangleMesh) surface.computeSurfaceMesh(mesh, 0.2f - smoothKick.get() * 0.1f); //notes[48] * 0.1f);
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
  public void draw() {
    background(0);
    translate(width / 2, height / 2);
    //camera(0, 0, 500, WIDTH/2, HEIGHT/2, DEPTH/2, 0, -1, 0);
    //camera(width/2f, height/2f, (height/2f) / tan((float) (PI*30.0 / 180.0)), width/2f, height/2f, 0, 0, 1, 0);
    ortho();
    pushMatrix();


    //translate(0, -200, -1000);
    rotateX(-PI/2 * orientation.get());
//    translate(width/2,height/2,0);
    //canvas.rotateX(mouseY*0.01);
    rotation += + rotationSpeed.get();
    rotateY(rotation);
//    rotateY(frameCount / 100f);
//    translate(-width/2,-height/2,0);
    scale(1.5f);


    try {
      mesh1 = computeMesh(mesh1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

//    stroke(255);
//    noFill();
//    strokeWeight(1);
    fill(0);
    noStroke();

    shader.set("spacing", 100f);
    shader.set("dimensions", 0.5f, 0f, 0.5f);

    shader(shader);

//    float kick = kickDecay.get();
//    float snare = snareDecay.get();
//
//    if (snare > 0) {
//      fill(lerpColor(color(255), color(0, 255, 255), snare));
//    } else if (kick > 0) {
//      fill(lerpColor(color(255), color(255, 0, 255), kick));
//    } else {
//      fill(255);
//    }

//    fill(color(255, 0, 255));

    drawMesh(mesh1, getGraphics());

    noFill();
    stroke(255);
    strokeWeight(4);
    //box(SCALE.x, SCALE.y, SCALE.z);

    popMatrix();

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
          PVector pos = new PVector(x, y, z);

          float blobs = 0;
//
          for (int j = 0; j < particles.length; j++) {
            blobs += 0.1 / pos.dist(particles[j]);
          }
          blobs += (float) (y*y) / dimx / 200f;

          float noise = (float) SimplexNoise.noise(
              x * NS * 2,
              y * NS * 2,
              z * NS * 2,
              frameCount * NS / 20f) * 0.5f;

          volumeData[i] = noise;
        }
      }
    }
  }
}
