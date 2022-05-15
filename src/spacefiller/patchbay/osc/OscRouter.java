package spacefiller.patchbay.osc;

import netP5.NetAddress;
import oscP5.*;
import spacefiller.patchbay.midi.DeviceMidiMessage;
import spacefiller.patchbay.oscquery.OscNode;
import spacefiller.patchbay.oscquery.OscQueryServer;
import spacefiller.patchbay.oscquery.OscRange;
import spacefiller.patchbay.signal.Node;
import spacefiller.patchbay.signal.PassThrough;

import java.util.*;

public class OscRouter {
  private OscP5 oscP5;
  private Map<String, Node> routes;
  private OscQueryServer oscQueryServer;
  private boolean printDebug;
  private Map<String, OscParser> registeredParsers;
  private boolean sync;
  private Queue<OscMessage> messageQueue;

  public OscRouter(String networkAddress, int port) {
    this.routes = new HashMap<>();
    this.oscQueryServer = new OscQueryServer("osc", networkAddress, port);
    this.oscP5 = listen(networkAddress, port);
    this.registeredParsers = new HashMap<>();
    this.messageQueue = new LinkedList<>();

    this.registeredParsers.put("f", (message, target) -> {
      float min = target.getExpectedMin();
      float max = target.getExpectedMax();
      float value = (message.get(0).floatValue() - min) / (max - min);
      target.setValue(value, true);
    });

    this.registeredParsers.put("i", (message, target) -> {
      float min = target.getExpectedMin();
      float max = target.getExpectedMax();
      float value = (message.get(0).intValue() - min) / (max - min);
      target.setValue(value, true);
    });

    this.registeredParsers.put("ii", (message, target) -> {
      float min = target.getExpectedMin();
      float max = target.getExpectedMax();
      float value = (message.get(0).intValue() - min) / (max - min);
      float velocity = message.get(1).intValue();
      target.setValue(new float[]{ value, velocity } , false);
    });
  }

  public void stop() {
    oscQueryServer.stop();
  }

  private void processOscMessage(OscMessage oscMessage) {
    if (printDebug) {
      System.out.println(oscMessage);
    }

    if (routes.containsKey(oscMessage.addrPattern())) {
      Node target = routes.get(oscMessage.addrPattern());
      if (registeredParsers.containsKey(oscMessage.typetag())) {
        registeredParsers.get(oscMessage.typetag()).parse(oscMessage, target);
      } else {
        throw new Error("No parser found for OSC type: " + oscMessage.typetag());
      }
    }
  }

  public void syncOn() {
    sync = true;
  }

  public void syncOff() {
    sync = false;
  }

  public void update() {
    while (!messageQueue.isEmpty()) {
      OscMessage message = messageQueue.poll();
      if (message != null) {
        processOscMessage(message);
      }
    }
  }

  private OscP5 listen(String networkAddress, int port) {
    OscP5 oscP5 = new OscP5(new OscEventListener() {
      @Override
      public void oscEvent(OscMessage oscMessage) {
        if (sync) {
          messageQueue.add(oscMessage);
        } else {
          processOscMessage(oscMessage);
        }
      }

      @Override
      public void oscStatus(OscStatus oscStatus) {

      }
    }, networkAddress, port, OscP5.UDP);

    return oscP5;
  }

  public void registerOscParser(String typeTag, OscParser parser) {
    registeredParsers.put(typeTag, parser);
  }

  public Node address(String address) {
    if (!routes.containsKey(address)) {
      routes.put(address, new PassThrough());
    }

    routes.get(address).addObserver((observable, arg) -> {
      Node node = (Node) observable;

      OscNode oscMethod = new OscNode(address, "");
      oscMethod.setTypes(OscType.fromType(node.getTypeHint()));

      // TODO: support more than float values
      oscMethod.setValues(node.toFloat().get());
      oscMethod.setRanges(new OscRange(
          node.getExpectedMin(),
          node.getExpectedMax()));
      oscQueryServer.put(oscMethod);
    });

    routes.get(address).notifyObservers(0);

    return routes.get(address);
  }

  public void log() {
    printDebug = true;
  }

  public void noLog() {
    printDebug = false;
  }
}
