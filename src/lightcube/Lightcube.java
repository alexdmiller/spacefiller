package lightcube;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.*;
import processing.core.PApplet;

public class Lightcube extends PApplet {
  private static final int BAUD_RATE = 57600;
  private static final String USB_PORT_NAME = "/dev/cu.usbmodem14201";
  private static final String XBEE_PORT_NAME = "/dev/tty.SLAB_USBtoUART";

  public static Lightcube usb() {
    return new SerialLightcube(USB_PORT_NAME, BAUD_RATE);
  }

  public static Lightcube wireless() {
    return new SerialLightcube(XBEE_PORT_NAME, BAUD_RATE);
  }

  public static Lightcube midi() {
    return new MidiLightcube();
  }

  protected Quaternion quaternion = new Quaternion(1, 0, 0, 0);
  protected Quaternion previousQuaternion;
  protected float rotationalVelocity;
  protected Vec3D up = new Vec3D(0, -1, 0);
  protected int color = 0x000000;
  protected int mode = 0;
  protected boolean transitionScene = false;
  protected int counter = 0;

  private float decay = 0.95f;

  public void update() {
    if (rotationalVelocity > 0) {
      rotationalVelocity = rotationalVelocity * decay;
    } else {
      rotationalVelocity = 0;
    }

    updateLightcube();
  }

  public boolean readTransitionScene() {
    boolean value = transitionScene;
    transitionScene = false;
    return value;
  }

  public int getColor() {
    return color;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public int getMode() {
    return mode;
  }

  public float getCounter() {
    return (float) counter / 255;
  }

  public Quaternion getQuaternion() {
    return quaternion;
  }

  public float[] getEulerRotation() {
    return new float[]{
        (float) Math.atan2(
            2 * quaternion.y * quaternion.w - 2 * quaternion.x * quaternion.z,
            1 - 2 * quaternion.y * quaternion.y - 2 * quaternion.z * quaternion.z),
        (float) Math.asin(2 * quaternion.x * quaternion.y + 2 * quaternion.z * quaternion.w),
        (float) Math.atan2(
            2 * quaternion.x * quaternion.w - 2 * quaternion.y * quaternion.z,
            1 - 2 * quaternion.x * quaternion.x - 2 * quaternion.z * quaternion.z)
    };
  }

  public float[] getNormalizedEuler() {
    return new float[]{
        (float) (Math.atan2(
            2 * quaternion.y * quaternion.w - 2 * quaternion.x * quaternion.z,
            1 - 2 * quaternion.y * quaternion.y - 2 * quaternion.z * quaternion.z) / Math.PI + 1) / 2,
        (float) (Math.asin(2 * quaternion.x * quaternion.y + 2 * quaternion.z * quaternion.w) / Math.PI + 1) / 2,
        (float) (Math.atan2(
            2 * quaternion.x * quaternion.w - 2 * quaternion.y * quaternion.z,
            1 - 2 * quaternion.x * quaternion.x - 2 * quaternion.z * quaternion.z) / Math.PI + 1) / 2
    };
  }

  public float getFlipAmount() {
    float[] axis = quaternion.toAxisAngle();
    Vec3D a = new Vec3D(-axis[1], axis[3], axis[2]);
    Quaternion q2 = Quaternion.createFromAxisAngle(a, axis[0]);
    Vec3D transformed = new Vec3D(0, 1, 0);
    q2.applyTo(transformed);

    return (up.dot(transformed) + 1) / 2f;
  }

  public void flipOrientation() {
    up.rotateX((float) Math.PI);
  }

  public float getRotationalVelocity() {
    return Math.min(rotationalVelocity, 100);
  }

  public void drawDebug(PGraphics graphics, float x, float y) {
    float size = 50;

    graphics.pushMatrix();

    {
      graphics.colorMode(PConstants.RGB);
      graphics.noFill();
      graphics.stroke(255);
      graphics.strokeWeight(1);
      graphics.translate(x, y);

      graphics.pushMatrix();
      {
        graphics.rotateX(up.headingYZ());
        graphics.stroke(255, 0, 255, 255);
        graphics.line(0, size / 2, 0, 0, -size / 2, 0);
        graphics.line(-10, -size / 2 + 10, 0, 0, -size / 2, 0);
        graphics.line(10, -size / 2 + 10, 0, 0, -size / 2, 0);
      }
      graphics.popMatrix();

      graphics.pushMatrix();
      {
//        graphics.rotateX((float) -(Math.PI / 4));
//        graphics.rotateY((float) (Math.PI / 4));
        graphics.stroke(255);

        graphics.pushMatrix();
        {
          float[] axis = quaternion.toAxisAngle();
          graphics.rotate(axis[0], -axis[1], axis[3], axis[2]);
          graphics.box(size * 2);
          graphics.line(0, size / 2, 0, 0, -size / 2, 0);
          graphics.line(-10, -size / 2 + 10, 0, 0, -size / 2, 0);
          graphics.line(10, -size / 2 + 10, 0, 0, -size / 2, 0);
        }
        graphics.popMatrix();

        graphics.noFill();
        graphics.translate(0, size * 3);
        graphics.box(size * 3);
      }
      graphics.popMatrix();
//      float flip = getFlipAmount();
//      graphics.translate(-50, size + 500);
//      graphics.noFill();
//      graphics.rect(0, 0, 100, 10);
//      graphics.fill(255);
//      graphics.rect(0, 0, 100 * flip, 10);
//
//      float velocity = getRotationalVelocity();
//      graphics.translate(0, 20);
//      graphics.noFill();
//      graphics.rect(0, 0, 100, 10);
//      graphics.fill(255);
//      graphics.rect(0, 0, velocity, 10);
    }
    graphics.popMatrix();
  }

  public void updateLightcube() { }
}