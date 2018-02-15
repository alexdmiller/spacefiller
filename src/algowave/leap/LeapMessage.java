package algowave.leap;

import com.leapmotion.leap.Controller;
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

  private static abstract class HandPositionMessage extends LeapMessage {
    public HandPositionMessage(String name) {
      super(name);
    }

    @Override
    public float getValue(Controller leap) {
      InteractionBox box = leap.frame().interactionBox();
      Vector position = box.normalizePoint(leap.frame().hands().frontmost().stabilizedPalmPosition());
      return getAxisValue(position);
    }

    public abstract float getAxisValue(Vector position);
  }

  public static LeapMessage[] ALL_MESSAGES = {X_AXIS, Y_AXIS};

  private String name;

  public LeapMessage(String name) {
    this.name = name;
  }

  public abstract float getValue(Controller leap);

  public String toString() {
    return name;
  }




}
