package lusio.generators;

import toxi.physics3d.*;
import toxi.physics3d.behaviors.*;
import toxi.physics3d.constraints.*;
import toxi.geom.*;
import toxi.geom.mesh.*;
import toxi.math.*;
import toxi.volume.*;
import lusio.Lusio;
import particles.Bounds;
import particles.Particle;
import particles.ParticleSystem;
import particles.behaviors.ParticleBehavior;
import particles.renderers.ParticleRenderer;
import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.AABB;
import toxi.geom.Quaternion;
import toxi.geom.Sphere;
import toxi.geom.Vec3D;
import toxi.geom.mesh.Face;
import toxi.geom.mesh.TriangleMesh;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import toxi.physics3d.behaviors.GravityBehavior3D;
import toxi.physics3d.constraints.ParticleConstraint3D;
import toxi.physics3d.constraints.SphereConstraint;
import toxi.volume.ArrayIsoSurface;
import toxi.volume.IsoSurface;
import toxi.volume.VolumetricSpaceArray;

import java.util.ArrayList;
import java.util.List;

public class FluidBoxGenerator extends SceneGenerator {
  int NUM_PARTICLES = 100;
  private float restLength = 300;
  int DIM=250;

  int GRID=20;
  float VS=2*DIM/GRID;
  Vec3D SCALE=new Vec3D(DIM,DIM,DIM).scale(2);
  float isoThreshold=5;

  int numP;
  VerletPhysics3D physics;
  ParticleConstraint3D boundingSphere;
  GravityBehavior3D gravity;

  VolumetricSpaceArray volume;
  IsoSurface surface;

  TriangleMesh mesh = new TriangleMesh("fluid");

  private boolean showPhysics=false;
  private boolean isWireFrame=false;
  private boolean isClosed=true;
  private boolean useBoundary=true;

  Vec3D colAmp=new Vec3D(400, 200, 200);

  private Quaternion quaternion;

  private float drawScale = 2;

  // TODO: why does this need to have a max force?
  public FluidBoxGenerator() {
    initPhysics();
    volume = new VolumetricSpaceArray(SCALE,GRID,GRID,GRID);
    surface = new ArrayIsoSurface(volume);
  }

  public void setRotation(Quaternion quaternion) {
    this.quaternion = quaternion;
  }

  public void setIsoThreshold(float isoThreshold) {
    this.isoThreshold = isoThreshold;
  }

  public void setRestLength(float restLength) {
    this.restLength = restLength;
  }

  public void setDrawScale(float drawScale) {
    this.drawScale = drawScale;
  }

  public void setWireFrame(boolean wireFrame) {
    isWireFrame = wireFrame;
  }

  @Override
  public void draw(PGraphics canvas) {
    canvas.pushMatrix();
    canvas.scale(drawScale);
    updateParticles();
    computeVolume();
    canvas.pushMatrix();

    float[] axis = quaternion.toAxisAngle();
    canvas.rotate(axis[0], -axis[1], axis[3], axis[2]);

    canvas.noFill();
    canvas.stroke(255,192);
    canvas.strokeWeight(1);
    // canvas.box(physics.getWorldBounds().getExtent().x*2);


    if (showPhysics) {
      canvas.strokeWeight(4);
      canvas.stroke(0);
      for(VerletParticle3D p : physics.particles) {
        Vec3D col=p.add(colAmp).scaleSelf(0.5f);
        canvas.stroke(col.x,col.y,col.z);
        canvas.point(p.x,p.y,p.z);
      }
    }
    else {
      canvas.ambientLight(216, 216, 216);
      canvas.directionalLight(255, 255, 255, 0, 1, 0);
      canvas.directionalLight(96, 96, 96, 1, 1, -1);
      if (isWireFrame) {
        canvas.stroke(255);
        canvas.noFill();
      }
      else {
        canvas.noStroke();
        canvas.fill(224,0,51);
      }
      canvas.beginShape(PConstants.TRIANGLES);
      if (!isWireFrame) {
        drawFilledMesh(canvas);
      }
      else {
        drawWireMesh(canvas);
      }
      canvas.endShape();
    }
    canvas.popMatrix();
    canvas.noLights();
    canvas.popMatrix();
  }

  void toggleBoundary() {
    useBoundary=!useBoundary;
    initPhysics();
  }

  void computeVolume() {
    float cellSize=(float)DIM*2/GRID;
    Vec3D pos=new Vec3D();
    Vec3D offset=physics.getWorldBounds().getMin();
    float[] volumeData=volume.getData();
    for(int z=0,index=0; z<GRID; z++) {
      pos.z=z*cellSize+offset.z;
      for(int y=0; y<GRID; y++) {
        pos.y=y*cellSize+offset.y;
        for(int x=0; x<GRID; x++) {
          pos.x=x*cellSize+offset.x;
          float val=0;
          for(int i=0; i<numP; i++) {
            Vec3D p=(Vec3D)physics.particles.get(i);
            float mag= (float) (pos.distanceToSquared(p)+0.00001);
            val+=1/mag;
          }
          volumeData[index++]=val;
        }
      }
    }
    if (isClosed) {
      volume.closeSides();
    }
    surface.reset();
    surface.computeSurfaceMesh(mesh, (float) (isoThreshold*0.001));
  }

  void drawFilledMesh(PGraphics canvas) {
    int num=mesh.getNumFaces();
    mesh.computeVertexNormals();
    for(int i=0; i<num; i++) {
      Face f=mesh.faces.get(i);
      Vec3D col=f.a.add(colAmp).scaleSelf(0.5f);
      canvas.fill(col.x,col.y,col.z);
      normal(canvas, f.a.normal);
      vertex(canvas, f.a);
      col=f.b.add(colAmp).scaleSelf(0.5f);
      canvas.fill(col.x,col.y,col.z);
      normal(canvas, f.b.normal);
      vertex(canvas, f.b);
      col=f.c.add(colAmp).scaleSelf(0.5f);
      canvas.fill(col.x,col.y,col.z);
      normal(canvas, f.c.normal);
      vertex(canvas, f.c);
    }
  }

  void drawWireMesh(PGraphics canvas) {
    canvas.fill(0);
    canvas.strokeWeight(4);
    int num=mesh.getNumFaces();
    for(int i=0; i<num; i++) {
      Face f=mesh.faces.get(i);
      Vec3D col=f.a.add(colAmp).scaleSelf(0.5f);
      canvas.stroke(col.x,col.y,col.z);
      vertex(canvas, f.a);
      col=f.b.add(colAmp).scaleSelf(0.5f);
      canvas.stroke(col.x,col.y,col.z);
      vertex(canvas, f.b);
      col=f.c.add(colAmp).scaleSelf(0.5f);
      canvas.stroke(col.x,col.y,col.z);
      vertex(canvas, f.c);
    }
  }

  void normal(PGraphics canvas, Vec3D v) {
    canvas.normal(v.x,v.y,v.z);
  }

  void vertex(PGraphics canvas, Vec3D v) {
    canvas.vertex(v.x,v.y,v.z);
  }

  void initPhysics() {
    physics=new VerletPhysics3D();
    physics.setWorldBounds(new AABB(new Vec3D(),new Vec3D(DIM,DIM,DIM)));
    if (surface!=null) {
      surface.reset();
      mesh.clear();
    }
    boundingSphere=new SphereConstraint(new Sphere(new Vec3D(),DIM),SphereConstraint.INSIDE);
    gravity=new GravityBehavior3D(new Vec3D(0,1,0));
    physics.addBehavior(gravity);
  }

  void updateParticles() {
    Vec3D grav = new Vec3D(0,0.5f,0);

    float[] axisAngle = quaternion.toAxisAngle();
    grav.rotateAroundAxis(new Vec3D(axisAngle[1], axisAngle[2], axisAngle[3]), -axisAngle[0]);

    gravity.setForce(grav.scaleSelf(2));
    numP=physics.particles.size();
    if (Math.random()<0.8 && numP<NUM_PARTICLES) {
      VerletParticle3D p=new VerletParticle3D(new Vec3D((float) Math.random()*20-10,-DIM, (float) Math.random()*20-10));
      if (useBoundary) p.addConstraint(boundingSphere);
      physics.addParticle(p);
    }
    if (numP>10 && physics.springs.size()<1400) {
      for(int i=0; i<60; i++) {
        if (Math.random()<0.1) {
          VerletParticle3D q=physics.particles.get((int) (Math.random() * numP));
          VerletParticle3D r=q;
          while(q==r) {
            r=physics.particles.get((int) (Math.random() * numP));
          }
          physics.addSpring(new VerletSpring3D(q,r,restLength, (float) 0.01));
        }
      }
    }
    float len=(float)numP/NUM_PARTICLES*restLength;
    for(VerletSpring3D s : physics.springs) {
      s.setRestLength((float) ((Math.random() * .2 + 0.9)*len));
    }
    physics.update();
  }
}
