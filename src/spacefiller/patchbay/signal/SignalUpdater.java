package spacefiller.patchbay.signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal utility class for updating signal nodes.
 */
public class SignalUpdater {
  protected interface Updateable {
    void update(float delta);
  }

  private static SignalUpdater instance;

  public SignalUpdater() {
    this.updateables = new ArrayList<>();
  }

  public static TestUpdater setTestMode() {
    if (instance == null) {
      TestUpdater updater = new TestUpdater();
      instance = updater;
      return updater;
    } else {
      throw new IllegalStateException("Cannot set test mode if instance has already been created.");
    }
  }

  public static SignalUpdater getInstance() {
    if (instance == null) {
      ThreadedUpdater updater = new ThreadedUpdater();
      new Thread(updater).start();
      instance = updater;
    }
    return instance;
  }


  protected List<Updateable> updateables;

  public void register(Updateable updateable) {
    synchronized (updateables) {
      updateables.add(updateable);
    }
  }

  private static class ThreadedUpdater extends SignalUpdater implements Runnable {
    private long lastUpdate;

    @Override
    public void run() {
      while (true) {
        long currentTime = System.currentTimeMillis();
        float elapsed = (currentTime - lastUpdate) / 1000f;
        lastUpdate = currentTime;

        synchronized (updateables) {
          for (Updateable updateable : updateables) {
            updateable.update(elapsed);
          }
        }

        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  protected static class TestUpdater extends SignalUpdater{
    public void tick(float dt) {
      for (Updateable updateable : updateables) {
        updateable.update(dt);
      }
    }
  }
}
