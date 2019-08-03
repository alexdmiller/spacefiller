package lightcube;

import spacefiller.remote.Serial;
import toxi.geom.Quaternion;

/**
 * Created by miller on 8/12/17.
 */
public class SerialLightcube extends Lightcube implements Serial.ByteEventListener {
  private float[] q = new float[4];
  private Serial port;
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

    updateCounter++;
  }

  public boolean isActive() {
    return port.active();
  }

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
