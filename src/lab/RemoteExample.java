package lab;

import processing.core.PApplet;
import processing.core.PVector;
import spacefiller.remote.MidiRemoteControl;
import spacefiller.remote.Mod;
import spacefiller.remote.OscRemoteControl;
import spacefiller.remote.VDMXWriter;

public class RemoteExample extends PApplet {
  public static void main(String[]args){
    main("lab.RemoteExample");
  }

  @Mod(min = 0, max = 20)
  public float flowForce = 3;

  @Mod(min = 2, max = 100)
  public float lineLength = 10;

  @Mod(min = 0, max = 500)
  public float circleRadius = 200;


  public float noiseScale = 500f;
  public float noiseSpeed1 = 0.01f;
  public float noiseSpeed2 = 0.01f;
  public float scrollSpeed = 1;
  public float fallSpeed = 10;
  public float lineSparsity = 1;
  public float lineThickness = 1;
  public float interpolation = 0f;
  public int numPoints = 50;
  public float scrambleSpeed = 0.01f;
  public float timeStep;
  public float scramble = 0;
  public float noise1Pos = 0;
  public float noise2Pos = 0;

  public void settings(){
    size(900, 900, P3D);
  }

  public void setup() {
//    OscRemoteControl remote = new OscRemoteControl(this, 10008);
//    VDMXWriter.exportVDMXJson("remote", remote.getTargetMap(), 10008);
  }

  public void draw() {
    timeStep += 0.01;
    scramble += scrambleSpeed;
    noise1Pos += noiseSpeed1;
    noise2Pos += noiseSpeed2;

    background(0);
    stroke(255);
    strokeWeight(lineThickness);

    for (int j = 0; j < numPoints; j++) {
      PVector p = PVector.add(
          PVector.mult(position3(j), interpolation),
          PVector.mult(position2(j), (1 - interpolation)));

      for (int i = 0; i < lineLength; i++) {

        float oldX = p.x;
        float oldY = p.y;
        PVector v = getFlow(p.x, p.y);

        p.x += v.x;
        p.y += v.y + fallSpeed;

        if (Math.sin(i + (noise((float) j) * 100.0) + timeStep * scrollSpeed) - lineSparsity < 0) {
          line(oldX, oldY, p.x, p.y);
        }
      }
    }
  }

  PVector getFlow(float x, float y) {
    float angle = noise(x / noiseScale, y / noiseScale - noise1Pos, noise2Pos) * PI * 6;
    return PVector.fromAngle(angle).setMag(flowForce);
  }

  PVector position2(int i) {
    float theta = 2 * PI * (float) i / numPoints + timeStep;
    PVector p = new PVector(
        cos(theta) * circleRadius + width / 2,
        sin(theta) * circleRadius + height / 2
    );
    return p;
  }

  PVector position3(int i) {
    PVector p = new PVector(
        noise(i, 0, scramble) * width - width / 2,
        noise(i, 1, scramble) * height - height / 2
    );
    return p;
  }
}