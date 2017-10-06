package algoplex2;

import processing.serial.Serial;
import spacefiller.remote.RemoteControl;
import spacefiller.remote.signal.DataReceiver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AlgoplexController extends RemoteControl {
  private Map<Integer, DataReceiver> serialPatches;
  private final static int MAX_VALUE = 254;
  private int currentByte = 0;
  private int[] packet = new int[6];

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

      //try {
        if (currentByte == packet.length + 1) {
          System.out.println(Arrays.toString(packet));

          for (int i = 0; i < packet.length; i++) {
            // if something changed, then write it
//          if (packet[i] != knobValues[i]) {
            if (serialPatches.containsKey(i)) {
              serialPatches.get(i).setValue((float) packet[i] / MAX_VALUE);
            }
//          }
            // knobValues[i] = packet[i];
          }
          currentByte = 0;
        }
//      } catch (Error e) {
//        System.out.println(e.getStackTrace());
//      }

       // System.out.println(Arrays.toString(bytes));
//      System.out.println("knob index = " + currentKnobIndex);
//      int in = port.read();
//      System.out.println(in);
//      //processInt(in);
//      //serialPatches.get(currentKnobIndex).setValue(in / (float) maxValue);
//      knobValues[currentKnobIndex] = in / (float) maxValue;
//      currentKnobIndex = (currentKnobIndex + 1) % knobValues.length;
    }
    //System.out.println(Arrays.toString(knobValues));
  }
}
