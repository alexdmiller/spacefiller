package sketches;

import spacefiller.color.SmoothColorTheme;
import lab.Flock;
import processing.core.PGraphics;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import spacefiller.remote.VDMXWriter;
import toxi.color.ColorRange;
import toxi.geom.*;
import toxi.math.ExponentialInterpolation;
import toxi.math.InterpolateStrategy;
import toxi.math.noise.PerlinNoise;
import toxi.physics3d.VerletParticle3D;
import toxi.physics3d.VerletPhysics3D;
import toxi.physics3d.VerletSpring3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tubes extends Scene {
  private static final Vec3D FORWARD = new Vec3D(0, 0, 1);
  private static final float SIZE = 200;
  private static final int NUM_POINTS = 1000;

  public static void main(String[] args) {
    main("sketches.Tubes");
  }

  private VerletPhysics3D physics;
  private List<VerletParticle3D> particles;
  private List<Worm> worms;
  private float t;
  private float jitterStrength = 200;

  @Mod(min = 0, max = 20)
  public float troughStrength = 0;

  @Mod(min = 1, max = 10)
  public float troughSpacing = 100;

  @Mod(min = 1, max = 20)
  public float springSize = 2;

  @Mod(min = 0, max = 200)
  public float desiredSeparation = 100;

  @Mod(min = 1, max = 20)
  public float circleSpacing = 10;

  @Mod(min = 0, max = 100)
  public float circleSize = 20;

  @Mod(min = 0, max = 1)
  public float springDeviation = 0.5f;

  @Mod(min = 0, max = 10)
  public float waveStrength;

  private Quaternion orientation = new Quaternion();
  private Quaternion targetOrientation = new Quaternion();
  private float interpolationAmount = 0;
  private InterpolateStrategy interpolateStrategy = new ExponentialInterpolation(0.8f);
  private Flock flock;

  public float interpolationSpeed = 0.05f;

  SmoothColorTheme theme;

  @Override
  public void doSetup() {
    physics = new VerletPhysics3D();
    particles = new ArrayList<>();
    physics.setWorldBounds(new AABB(new Vec3D(0, 0, 0), SIZE));
    physics.setDrag(0.01f);

    worms = new ArrayList<>();
    newWorms(5);

    flock = new Flock(0, 0, 1, 0, 0, 200, 10, 10);

    theme = new SmoothColorTheme(ColorRange.BRIGHT, 10, 200);
    // theme = new Sm

    OscRemoteControl remote = new OscRemoteControl(12010);
    remote.autoRoute(this);
    // VDMXWriter.exportVDMXJson("tubes", remote.getTargetNodes(), 12010);
  }

  private void separate(List<VerletParticle3D> particles) {
    for (int i = 0; i < particles.size(); i++) {
      for (int j = i + 1; j < particles.size(); j++) {
        Vec3D delta = particles.get(j).sub(particles.get(i));
        if (delta.magnitude() < desiredSeparation) {
          Vec3D force = delta.scale(0.01f);
          particles.get(i).addForce(force.scale(-1));
          particles.get(j).addForce(force.scale(1));
        }
      }
    }
  }

  @Override
  protected void drawCanvas(PGraphics graphics, float mouseX, float mouseY) {
    t += 0.01;
    background(0);

    Quaternion current = orientation;

    if (!orientation.equals(targetOrientation)) {
      if (interpolationAmount < 1) {
        interpolationAmount += interpolationSpeed;
        current = orientation.interpolateTo(targetOrientation, interpolationAmount, interpolateStrategy);
      }

      if (interpolationAmount >= 1) {
        interpolationAmount = 0;
        orientation = targetOrientation;
      }
    }

    float[] axis = current.toAxisAngle();
    graphics.rotate(axis[0], axis[1], axis[2], axis[3]);

    synchronized (worms) {
      separate(particles);
      flock.apply(worms);
      physics.update();

      for (Worm worm : worms) {
        worm.draw(graphics, theme, physics, t, troughStrength, troughSpacing, springSize, circleSpacing, circleSize, springDeviation, waveStrength);
      }
    }
  }

  @Mod(min = 0, max = 2)
  public void setCohesionStrength(float strength) {
    flock.cohesionWeight = strength;
  }

  @Mod
  public void jitterTubes() {
    for (int i = 0; i < particles.size(); i++) {
      particles.get(i).addForce(new Vec3D(
          random(-jitterStrength, jitterStrength),
          random(-jitterStrength, jitterStrength),
          random(-jitterStrength, jitterStrength)));
    }
  }

  @Mod
  public void newOrientation() {
    targetOrientation = Quaternion.createFromEuler(
        floor(random(4)) / 4f * PI * 2,
        floor(random(4)) / 4f * PI * 2,
        floor(random(4)) / 4f * PI * 2);
  }

  @Mod(min = 1, max = 20)
  public void newWorms(float numWorms) {
    synchronized (worms) {
      physics = new VerletPhysics3D();
      physics.setWorldBounds(new AABB(new Vec3D(0, 0, 0), SIZE));
      physics.setDrag(0.01f);

      worms.clear();
      particles.clear();

      for (int i = 0; i < numWorms; i++) {
        worms.add(Worm.make(physics, NUM_POINTS / (int) numWorms));
      }

      for (Worm worm : worms) {
        particles.addAll(Arrays.asList(worm.getSegments()));
      }
    }
  }

  public static class Worm {
    private VerletParticle3D segments[];
    private PerlinNoise perlin;

    public static Worm make(VerletPhysics3D physics, int length) {
      Worm worm = new Worm();
      worm.segments = new VerletParticle3D[length];

      worm.segments[0] = new VerletParticle3D(
          (float) Math.random() * physics.getWorldBounds().getExtent().x - physics.getWorldBounds().getExtent().x / 2,
          (float) Math.random() * physics.getWorldBounds().getExtent().y - physics.getWorldBounds().getExtent().y / 2,
          (float) Math.random() * physics.getWorldBounds().getExtent().z - physics.getWorldBounds().getExtent().z / 2);
      physics.addParticle(worm.segments[0]);
      for (int i = 1; i < worm.segments.length; i++) {
        worm.segments[i] = new VerletParticle3D(
            (float) Math.random() * physics.getWorldBounds().getExtent().x - physics.getWorldBounds().getExtent().x / 2,
            (float) Math.random() * physics.getWorldBounds().getExtent().y - physics.getWorldBounds().getExtent().y / 2,
            (float) Math.random() * physics.getWorldBounds().getExtent().z - physics.getWorldBounds().getExtent().z / 2);
        physics.addParticle(worm.segments[i]);
        VerletSpring3D spring = new VerletSpring3D(worm.segments[i], worm.segments[i - 1], 5, 1f);
        physics.addSpring(spring);
      }
      return worm;
    }

    public Worm() {
      this.perlin = new PerlinNoise();
    }

    public VerletParticle3D[] getSegments() {
      return segments;
    }

    public void draw(
        PGraphics graphics,
        SmoothColorTheme theme,
        VerletPhysics3D physics,
        float t,
        float troughStrength,
        float troughSpacing,
        float springSpacing,
        float circleSpacing,
        float circleSize,
        float springDeviation,
        float waveStrength) {
      Vec3D velocity = getHead().getVelocity();
      Vec3D desiredVelocity = new Vec3D(0, 0, 1);
      desiredVelocity.rotateX(perlin.noise(t / 1000 ) * PI * 6);
      desiredVelocity.rotateY(perlin.noise(t  / 1000 + 100) * PI * 6);

      desiredVelocity.normalize();
      desiredVelocity.scaleSelf(10);
      Vec3D steer = velocity.sub(desiredVelocity);

      steer.limit(1);

      getHead().addForce(steer);
      getHead().scaleVelocity(5 / (getHead().getVelocity().magnitude() + 0.01f));

      graphics.noStroke();
      graphics.fill(255);
      graphics.pushMatrix();
      graphics.translate(getHead().x, getHead().y, getHead().z);
      graphics.sphere(10);

      desiredVelocity.scaleSelf(100);
      graphics.popMatrix();

      graphics.stroke(255);

      for (int i = 1; i < segments.length - 1; i++) {
        graphics.stroke(theme.getColor((float) ((float) i / segments.length * 2 * Math.PI) + t * 10).toARGB());
        VerletSpring3D spring = physics.getSpring(segments[i], segments[i - 1]);
        spring.setRestLength(sin(i / 10f + t * 10) * springDeviation * springSpacing + springSpacing);

        segments[i].addForce(new Vec3D(
            sin(segments[i].x / troughSpacing) * 1,
            sin(segments[i].y / troughSpacing) * 1,
            sin(segments[i].z / troughSpacing) * 1
        ).scale(troughStrength));

        segments[i].addVelocity(new Vec3D(
            sin((segments[i].y + segments[i].z) * 1) * 1, 0,0).scale(waveStrength));

        graphics.strokeWeight(3);
        graphics.line(segments[i].x, segments[i].y, segments[i].z, segments[i - 1].x, segments[i - 1].y, segments[i - 1].z);

        graphics.pushMatrix();
        graphics.translate(segments[i].x, segments[i].y, segments[i].z);
        Vec3D before = segments[i - 1];
        Vec3D after = segments[i + 1];
        Vec3D delta = after.sub(before);
        Quaternion rotation = Quaternion.getAlignmentQuat(delta, FORWARD);

        float[] axis = rotation.toAxisAngle();
        graphics.rotate(axis[0], axis[1], axis[2], axis[3]);

        //graphics.stroke(255, 200);

        graphics.fill(0);

        float size = sin(i / (int) circleSpacing + t * 2) * circleSize + circleSize / 10;
        graphics.ellipse(0, 0, size, size);

        graphics.popMatrix();
      }
    }

    public VerletParticle3D getHead() {
      return segments[0];
    }
  }
}
