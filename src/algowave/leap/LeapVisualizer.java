package algowave.leap;

import com.leapmotion.leap.*;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

/**
 * Created by miller on 2/15/18.
 */
public class LeapVisualizer {
  private Controller leap;
  private PGraphics canvas;

  public LeapVisualizer(Controller leap) {
    this.leap = leap;
  }

  public PImage getFrame() {
    return canvas;
  }

  public void setOutput(PGraphics canvas) {
    this.canvas = canvas;
  }

  public void draw() {
    canvas.beginDraw();
    canvas.background(0);
    canvas.stroke(255);
    for (Hand hand : leap.frame().hands()) {
      Vector position = hand.stabilizedPalmPosition();
      canvas.pushMatrix();
      canvas.translate(position.getX(), position.getY(), position.getZ());
      canvas.sphereDetail(20);
      canvas.sphere(10);
      canvas.popMatrix();

      // Are there any fingers?
      if (hand.fingers().count() == 5) {
        canvas.stroke(0, 35);
        canvas.noFill();
        Vector lastJointOfThumb = hand.finger(0).bone(Bone.Type.TYPE_PROXIMAL).prevJoint();
        Vector lastJointOfIndex = hand.finger(1).bone(Bone.Type.TYPE_METACARPAL).prevJoint();
        Vector lastJointOfMiddle = hand.finger(2).bone(Bone.Type.TYPE_METACARPAL).prevJoint();
        Vector lastJointOfRing = hand.finger(3).bone(Bone.Type.TYPE_METACARPAL).prevJoint();
        Vector lastJointOfPinky = hand.finger(4).bone(Bone.Type.TYPE_METACARPAL).prevJoint();

        canvas.beginShape();
        canvas.vertex(lastJointOfThumb.getX(), lastJointOfThumb.getY(), lastJointOfThumb.getZ());
        canvas.vertex(lastJointOfIndex.getX(), lastJointOfIndex.getY(), lastJointOfIndex.getZ());
        canvas.vertex(lastJointOfMiddle.getX(), lastJointOfMiddle.getY(), lastJointOfMiddle.getZ());
        canvas.vertex(lastJointOfRing.getX(), lastJointOfRing.getY(), lastJointOfRing.getZ());
        canvas.vertex(lastJointOfPinky.getX(), lastJointOfPinky.getY(), lastJointOfPinky.getZ());
        canvas.endShape(PConstants.OPEN);
      }
//      for (Finger finger : this.getFingers()) {
//        finger.draw();
//      }

    }

    canvas.stroke(255);
    canvas.noFill();
    canvas.rect(0, 0, canvas.width, canvas.height);
    canvas.endDraw();
  }
}
