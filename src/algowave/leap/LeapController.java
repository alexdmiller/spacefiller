package algowave.leap;

import com.leapmotion.leap.BugReport;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.InteractionBox;
import com.leapmotion.leap.Listener;
import spacefiller.remote.RemoteControl;
import spacefiller.remote.signal.DataReceiver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LeapController extends RemoteControl implements Runnable {
  private Controller leap;
  private Map<LeapMessage, DataReceiver> leapPatches;
  private Thread pollingThread;
  private long lastFrameID = 0;

  public LeapController() {
    leap = new Controller();
    leapPatches = new HashMap<>();

    for (LeapMessage message : LeapMessage.ALL_MESSAGES) {
      controller(message);
    }

    pollingThread = new Thread(this);
    pollingThread.start();
  }

  public DataReceiver controller(LeapMessage message) {
    synchronized (leapPatches) {
      DataReceiver dataReceiver = leapPatches.get(message);
      if (dataReceiver == null) {
        dataReceiver = createReceiver();
        leapPatches.put(message, dataReceiver);
      }

      return dataReceiver;
    }
  }

  public Set<LeapMessage> getPatchedMessages() {
    return leapPatches.keySet();
  }

  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
      if (leap.frame().id() != lastFrameID) {
        synchronized (leapPatches) {
          for (LeapMessage message : leapPatches.keySet()) {
            float messageValue = message.getValue(leap);
            leapPatches.get(message).setValue(messageValue);
          }

          lastFrameID = leap.frame().id();
        }
      }
    }
  }
}
