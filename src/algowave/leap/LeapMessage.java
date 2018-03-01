package algowave.leap;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Vector;

public abstract class LeapMessage {
  public static LeapMessage X_AXIS = new HandPositionMessage("Position X") {
    @Override
    public float getAxisValue(Vector position) {
      return position.getX();
    }
  };

  public static LeapMessage Y_AXIS = new HandPositionMessage("Position Y") {
    @Override
    public float getAxisValue(Vector position) {
      return position.getY();
    }
  };

  public static LeapMessage Z_AXIS = new HandPositionMessage("Position Z") {
    @Override
    public float getAxisValue(Vector position) {
      return position.getZ();
    }
  };

  public static LeapMessage Y_VELOCITY = new HandPositionMessage("Velocity Y") {
    float lastY = 0;

    @Override
    public float getAxisValue(Vector position) {
      float currentY = position.getY();
      float velocity = Math.abs(lastY - currentY);
      lastY = currentY;
      return velocity * 3;
    }
  };

  public static LeapMessage ROLL = new HandMessage("Roll") {
    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      return (float) ((hand.palmNormal().roll() + Math.PI) / (2 * Math.PI));
    }
  };

  public static LeapMessage PITCH = new HandMessage("Pitch") {
    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      return (float) ((hand.palmNormal().pitch() + Math.PI) / (2 * Math.PI));
    }
  };


  public static LeapMessage YAW = new HandMessage("Yaw") {
    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      return (float) ((hand.palmNormal().yaw() + Math.PI) / (2 * Math.PI));
    }
  };

  public static LeapMessage GRAB = new HandMessage("Grab") {
    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      return hand.grabStrength();
    }
  };

  private static abstract class HandPositionMessage extends HandMessage {
    public HandPositionMessage(String name) {
      super(name);
    }

    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      Vector position = box.normalizePoint(hand.stabilizedPalmPosition());
      return getAxisValue(position);
    }

    public abstract float getAxisValue(Vector position);
  }

  public static LeapMessage SPEED = new HandMessage("Speed") {
    Vector lastPosition;

    @Override
    public final float getHandValue(Hand hand, InteractionBox box) {
      if (hand.isValid()) {
        Vector position = box.normalizePoint(hand.stabilizedPalmPosition());

        if (lastPosition != null) {
          Vector delta = position.minus(lastPosition);
          lastPosition = position;
          return delta.magnitude();
        } else {
          lastPosition = position;
          return 0;
        }
      } else {
        lastPosition = null;
        return 0;
      }

    }
  };

  private static abstract class HandMessage extends LeapMessage {
    public HandMessage(String name) {
      super(name);
    }

    @Override
    public final float getValue(Controller leap) {
      InteractionBox box = leap.frame().interactionBox();
      Hand hand = leap.frame().hands().frontmost();
      return getHandValue(hand, box);
    }

    public abstract float getHandValue(Hand hand, InteractionBox box);
  }

  public static LeapMessage[] ALL_MESSAGES = { X_AXIS, Y_AXIS, Z_AXIS, Y_VELOCITY, ROLL, PITCH, YAW, GRAB, SPEED };

  private String name;

  public LeapMessage(String name) {
    this.name = name;
  }

  public abstract float getValue(Controller leap);

  public String toString() {
    return name;
  }
}
