package lightcube;

import spacefiller.remote.Serial;
import toxi.geom.Quaternion;

/**
 * Created by miller on 8/12/17.
 */
public class SerialLightcube extends Lightcube implements Serial.ByteEventListener {
  private float[] q = new float[4];
  private int interval = 0;
  private Serial port;
  private int serialCount = 0;                 // current packet byte position
  private int aligned = 0;
  private int updateCounter = 0;

  private String portName;
  private int baudRate;

  public SerialLightcube(String portName, int baudRate) {
    this.portName = portName;
    this.baudRate = baudRate;

    port = new Serial(portName, baudRate);
    port.onBytes('\n', this);
  }

  public void updateLightcube() {
    if (updateCounter >= 2) {
      updateCounter = 0;

      if (port != null) {
        port.write(mode);
      }
    }
//    port.write(0);

    updateCounter++;
  }

  public boolean isActive() {
    return port.active();
  }


//  public void serialEvent(Serial port) {
//    /**
//     * Packet structure:
//     * { '$', 0x02, 0,0, 0,0, 0,0, 0,0, 0x00, 0x00, 0, 0, 0, 0, '\r', '\n' };
//     */
//    while (port.available() > 0) {
//      int ch = port.read();
//      if (ch == '$') {serialCount = 0;} // this will help with alignment
//      if (aligned < 4) {
//        // make sure we are properly aligned on a 17-byte packet
//        if (serialCount == 0) {
//          if (ch == '$') aligned++; else aligned = 0;
//        } else if (serialCount == 1) {
//          if (ch == 2) aligned++; else aligned = 0;
//        } else if (serialCount == 17) {
//          if (ch == '\r') aligned++; else aligned = 0;
//        } else if (serialCount == 18) {
//          if (ch == '\n') aligned++; else aligned = 0;
//        }
//        //println(ch + " " + aligned + " " + serialCount);
//        serialCount++;
//        if (serialCount == 19) serialCount = 0;
//      } else {
//        if (serialCount > 0 || ch == '$') {
//          teapotPacket[serialCount++] = ch;
//          if (serialCount == 19) {
//            serialCount = 0; // restart packet byte position
//
//
//          }
//        }
//      }
//    }
//  }

  @Override
  public void onBytes(byte[] teapotPacket) {
    if (teapotPacket.length == 18) {
      // get quaternion from data packet
      q[0] = (((teapotPacket[2] & 0xFF) << 8) | (teapotPacket[3]& 0xFF)) / 16384.0f;
      q[1] = (((teapotPacket[4]& 0xFF) << 8) | (teapotPacket[5]& 0xFF)) / 16384.0f;
      q[2] = (((teapotPacket[6]& 0xFF) << 8) | (teapotPacket[7]& 0xFF)) / 16384.0f;
      q[3] = (((teapotPacket[8]& 0xFF) << 8) | (teapotPacket[9]& 0xFF)) / 16384.0f;
      for (int i = 0; i < 4; i++) if (q[i] >= 2) q[i] = -4 + q[i];

      previousQuaternion = quaternion;
      quaternion = new Quaternion(q[0], q[1], q[2], q[3]);

      rotationalVelocity = Math.max(quaternion.sub(previousQuaternion).magnitude() * 500, rotationalVelocity);

      counter = teapotPacket[16];
    }
  }
}
