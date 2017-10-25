package algoplex2;

import processing.serial.Serial;
import spacefiller.remote.RemoteControl;
import spacefiller.remote.signal.DataReceiver;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AlgoplexController extends RemoteControl {
  private Map<Integer, DataReceiver> serialPatches;
  private final static int MAX_VALUE = 254;
  private final static int KNOBS = 6;
  private final static int BUTTON_INDEX = 6;
  private int currentByte = 0;
  private int[] packet = new int[7];
  private boolean lastButtonState = false;

  public AlgoplexController(String portName, int baudRate) {
    super();
    this.serialPatches = new HashMap<>();
    listen(portName, baudRate);

  }

  public void listen(String portName, int baudRate) {
    new Serial(this, portName, baudRate);
  }

  public DataReceiver controller(int number) {
    DataReceiver dataReceiver = serialPatches.get(number);
    if (dataReceiver == null) {
      dataReceiver = createReceiver();
      serialPatches.put(number, dataReceiver);
    }
    return dataReceiver;
  }

  public void serialEvent(Serial port) {
    while (port.available() > 0) {
      int v = port.read();

      if (v == 255) {
        if (currentByte != 0) {
          System.out.println("MISALIGNED");
        }
        currentByte++;
      } else if (currentByte > 0) {
        packet[currentByte - 1] = v;
        currentByte++;
      }

      if (currentByte == packet.length + 1) {
        for (int i = 0; i < KNOBS; i++) {
          if (serialPatches.containsKey(i)) {
            serialPatches.get(i).setValue((float) packet[i] / MAX_VALUE);
          }
        }

        if (packet[BUTTON_INDEX] == 0) {
          lastButtonState = false;
        } else if (lastButtonState == false) {
          serialPatches.get(BUTTON_INDEX).setValue(null);
          lastButtonState = true;
        }

        currentByte = 0;
      }
    }

    // System.out.println(Arrays.toString(packet));
  }


}
