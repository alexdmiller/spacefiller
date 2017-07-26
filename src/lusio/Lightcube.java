package lusio;

import processing.core.PConstants;
import processing.core.PGraphics;
import toxi.geom.*;
import processing.core.PApplet;
import processing.serial.*;

public class Lightcube extends PApplet {
  Serial port;

  private char[] teapotPacket = new char[14];  // InvenSense Teapot packet
  private int serialCount = 0;                 // current packet byte position
  private int aligned = 0;
  private int interval = 0;

  private float[] q = new float[4];
  private Quaternion quaternion = new Quaternion(1, 0, 0, 0);
  private Quaternion previousQuaternion;
  private float rotationalVelocity;
  private Vec3D up = new Vec3D(0, -1, 0);

  private float decay = 0.9f;

  public Lightcube(String portName) {
    try {
      port = new Serial(this, portName, 115200);
      port.write('r');
    } catch (RuntimeException e) {
      port = null;
    }
  }

  public void update() {
    if (port != null) {
      if (millis() - interval > 1000) {
        // resend single character to trigger DMP init/start
        // in case the MPU is halted/reset while applet is running
        port.write('r');
        interval = millis();
      }
    }

    if (rotationalVelocity > 0) {
      rotationalVelocity = rotationalVelocity * decay;
    } else {
      rotationalVelocity = 0;
    }
  }

  public void serialEvent(Serial port) {
    interval = millis();

    while (port.available() > 0) {
      int ch = port.read();
      if (ch == '$') {serialCount = 0;} // this will help with alignment
      if (aligned < 4) {
        // make sure we are properly aligned on a 14-byte packet
        if (serialCount == 0) {
          if (ch == '$') aligned++; else aligned = 0;
        } else if (serialCount == 1) {
          if (ch == 2) aligned++; else aligned = 0;
        } else if (serialCount == 12) {
          if (ch == '\r') aligned++; else aligned = 0;
        } else if (serialCount == 13) {
          if (ch == '\n') aligned++; else aligned = 0;
        }
        //println(ch + " " + aligned + " " + serialCount);
        serialCount++;
        if (serialCount == 14) serialCount = 0;
      } else {
        if (serialCount > 0 || ch == '$') {
          teapotPacket[serialCount++] = (char)ch;
          if (serialCount == 14) {
            serialCount = 0; // restart packet byte position

            // get quaternion from data packet
            q[0] = ((teapotPacket[2] << 8) | teapotPacket[3]) / 16384.0f;
            q[1] = ((teapotPacket[4] << 8) | teapotPacket[5]) / 16384.0f;
            q[2] = ((teapotPacket[6] << 8) | teapotPacket[7]) / 16384.0f;
            q[3] = ((teapotPacket[8] << 8) | teapotPacket[9]) / 16384.0f;
            for (int i = 0; i < 4; i++) if (q[i] >= 2) q[i] = -4 + q[i];


            previousQuaternion = quaternion;
            quaternion = new Quaternion(q[0], q[1], q[2], q[3]);

            rotationalVelocity = Math.max(quaternion.sub(previousQuaternion).magnitude() * 500, rotationalVelocity);
          }
        }
      }
    }
  }

  public Quaternion getQuaternion() {
    return quaternion;
  }

  public float[] getEulerRotation() {
    return new float[] {
        (float) Math.atan2(
            2 * quaternion.y * quaternion.w - 2 * quaternion.x * quaternion.z,
            1 - 2 * quaternion.y * quaternion.y - 2 * quaternion.z * quaternion.z),
        (float) Math.asin(2 * quaternion.x * quaternion.y + 2 * quaternion.z * quaternion.w),
        (float) Math.atan2(
            2 * quaternion.x * quaternion.w - 2 * quaternion.y * quaternion.z,
            1 - 2 * quaternion.x * quaternion.x - 2 * quaternion.z * quaternion.z)
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
    return rotationalVelocity;
  }

  public void drawDebug(PGraphics graphics, float x, float y) {
    float size = 50;

    graphics.pushMatrix();

    graphics.colorMode(PConstants.RGB);
    graphics.noFill();
    graphics.stroke(255);
    graphics.strokeWeight(1);
    graphics.translate(x, y);

    graphics.pushMatrix();

    graphics.rotateX(up.headingYZ());
    graphics.stroke(255, 0, 255, 255);
    graphics.line(0, size / 2, 0, 0, -size / 2, 0);
    graphics.line(-10, -size / 2 + 10, 0, 0, -size / 2, 0);
    graphics.line(10, -size / 2 + 10, 0, 0, -size / 2, 0);
    graphics.popMatrix();

    graphics.pushMatrix();
    graphics.stroke(255);
    float[] axis = quaternion.toAxisAngle();
    graphics.rotate(axis[0], -axis[1], axis[3], axis[2]);
    graphics.box(size * 2);
    graphics.line(0, size / 2, 0, 0, -size / 2, 0);
    graphics.line(-10, -size / 2 + 10, 0, 0, -size / 2, 0);
    graphics.line(10, -size / 2 + 10, 0, 0, -size / 2, 0);
    graphics.popMatrix();

    float flip = getFlipAmount();
    graphics.translate(-50, size + 20);
    graphics.noFill();
    graphics.rect(0, 0, 100, 10);
    graphics.fill(255);
    graphics.rect(0, 0, 100 * flip, 10);

    float velocity = getRotationalVelocity();
    graphics.translate(0, 20);
    graphics.noFill();
    graphics.rect(0, 0, 100, 10);
    graphics.fill(255);
    graphics.rect(0, 0, velocity, 10);

    graphics.popMatrix();
  }
}
