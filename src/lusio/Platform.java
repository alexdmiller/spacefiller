package lusio;

import processing.core.PApplet;
import processing.serial.Serial;
import spacefiller.remote.SerialStringRemoteControl;
import spacefiller.remote.signal.Gate;

import java.util.ArrayList;
import java.util.List;

public class Platform {
  public static final int NUM_SLOTS = 2;
  private static final float PRESSURE_THRESHOLD = 0.5f;
  private SerialStringRemoteControl remote;
  private List<CubePlacedListener> listeners;

  public Platform(String portName, int baudRate) {
    remote = new SerialStringRemoteControl(portName, baudRate, NUM_SLOTS);
    listeners = new ArrayList<>();

    for (int i = 0; i < NUM_SLOTS; i++) {
      int slot = i;
      Gate gate = remote.controller(i).gate(PRESSURE_THRESHOLD);
      gate.debounce(100);
      gate.onGateTriggered(() -> {
        for (CubePlacedListener listener : listeners) {
          listener.cubePlaced(slot);
        }
      });
    }

    remote.connect();
  }

  public boolean isConnected() {
    return remote.isConnected();
  }

  public void connect() {
    remote.connect();
  }


  public void onCubePlaced(CubePlacedListener listener) {
    listeners.add(listener);
  }

  public interface CubePlacedListener {
    void cubePlaced(int slot);
  }
}
