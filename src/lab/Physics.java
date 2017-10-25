package lab;

import processing.core.PApplet;
import processing.opengl.PJOGL;
import toxi.geom.*;
import toxi.math.noise.PerlinNoise;
import toxi.physics3d.behaviors.AttractionBehavior3D;
import toxi.physics2d.*;
import toxi.physics2d.behaviors.GravityBehavior2D;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;
import toxi.physics3d.behaviors.GravityBehavior3D;
import toxi.physics3d.behaviors.ParticleBehavior3D;

public class Physics extends PApplet {
  private static final Vec3D FORWARD = new Vec3D(0, 0, 1);

  public static void main(String[] args) {
    main("lab.Physics");
  }

  private VerletPhysics3D physics;
  private VerletParticle3D[] particles;
  private float t;
  private PerlinNoise perlin;

  public void settings() {
    size(1920, 1080, P3D);
    PJOGL.profile = 2;
  }

  public void setup() {
    perlin = new PerlinNoise();
    physics = new VerletPhysics3D();
    particles = new VerletParticle3D[1000];
    physics.setWorldBounds(new AABB(new Vec3D(0, 0, 0), 200));
    //physics.setDrag(0.01f);

    particles[0] = new VerletParticle3D(400, 0, 0);
    physics.addParticle(particles[0]);
    for (int i = 1; i < particles.length; i++) {
      particles[i] = new VerletParticle3D(random(-250, 250), random(-250, 250), random(-250, 250));
      physics.addParticle(particles[i]);

      VerletSpring3D spring = new VerletSpring3D(particles[i], particles[i - 1], 2, 1f);
      physics.addSpring(spring);
    }
  }

  private void separate(VerletParticle3D[] particles) {
    for (int i = 0; i < particles.length; i++) {
      for (int j = i + 1; j < particles.length; j++) {
        Vec3D delta = particles[j].sub(particles[i]);
        if (delta.magnitude() < 100) {
          Vec3D force = delta.scale(0.001f);
          particles[i].addForce(force.scale(-1));
          particles[j].addForce(force.scale(1));
        }
      }
    }
  }

  private Vec3D desiredHeading = new Vec3D(0, 0, 0);


  private void steer() {
    Vec3D currentHeading = particles[0].getVelocity();
    float wobbleSpeed = 1f;
    Vec3D desiredHeading = FORWARD
        .getRotatedX(perlin.noise(t * wobbleSpeed) * PI * 4)
        .getRotatedY(perlin.noise(t * wobbleSpeed + 100) * PI * 4)
        .getRotatedZ(perlin.noise(t * wobbleSpeed + 200) * PI * 4);
    desiredHeading = desiredHeading.scale(100);

    Vec3D steer = desiredHeading.sub(currentHeading);
    steer.limit(10);

    particles[0].addForce(steer);

    pushMatrix();
    translate(particles[0].x, particles[0].y, particles[0].z);
    stroke(255, 0, 0);
    line(0, 0, 0, desiredHeading.x, desiredHeading.y, desiredHeading.z);
    popMatrix();
  }

  public void draw() {
    t += 0.01;
    background(0);

    translate(width / 2, height / 2, 250);
    //rotateY(t);

    steer();
    separate(particles);
    physics.update();
    stroke(255);

    for (int i = 1; i < particles.length - 1; i++) {
      strokeWeight(3);
      point(particles[i].x, particles[i].y, particles[i].z);

      strokeWeight(1);
      line(particles[i].x, particles[i].y, particles[i].z, particles[i - 1].x, particles[i - 1].y, particles[i - 1].z);

      pushMatrix();
      translate(particles[i].x, particles[i].y, particles[i].z);
      Vec3D before = particles[i - 1];
      Vec3D after = particles[i + 1];
      Vec3D delta = after.sub(before);
      Quaternion rotation = Quaternion.getAlignmentQuat(delta, FORWARD);

      float[] axis = rotation.toAxisAngle();
      rotate(axis[0], axis[1], axis[2], axis[3]);

      stroke(255, 200);
      fill(255, 10);

      float size = sin(i / 50f + t * 2) * 10 + 20;
      ellipse(0, 0, size, size);
      popMatrix();
    }

//    for (int i = 4; i < particles.length; i++) {
//      strokeWeight(1);
//      line(particles[i].x, particles[i].y, particles[i].z, particles[i - 4].x, particles[i - 4].y, particles[i - 4].z);
//    }

  }

  public void mousePressed() {
    for (int i = 0; i < particles.length; i++) {
      particles[i].addForce(new Vec3D(random(-100, 100), random(-100, 100), random(-100, 100)));
    }
  }
}
