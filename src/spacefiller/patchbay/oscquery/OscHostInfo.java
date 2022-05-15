package spacefiller.patchbay.oscquery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import static spacefiller.patchbay.oscquery.Constants.*;

public class OscHostInfo {
  private String name;
  private String oscIp;
  private int oscPort;
  private String oscTransport;
  private String webSocketIp;
  private int webSocketPort;

  public OscHostInfo(String name, String oscIp, int oscPort, String oscTransport, String webSocketIp, int webSocketPort) {
    this.name = name;
    this.oscIp = oscIp;
    this.oscPort = oscPort;
    this.oscTransport = oscTransport;
    this.webSocketIp = webSocketIp;
    this.webSocketPort = webSocketPort;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOscIp() {
    return oscIp;
  }

  public void setOscIp(String oscIp) {
    this.oscIp = oscIp;
  }

  public int getOscPort() {
    return oscPort;
  }

  public void setOscPort(int oscPort) {
    this.oscPort = oscPort;
  }

  public String getOscTransport() {
    return oscTransport;
  }

  public void setOscTransport(String oscTransport) {
    this.oscTransport = oscTransport;
  }

  public String getWebSocketIp() {
    return webSocketIp;
  }

  public void setWebSocketIp(String webSocketIp) {
    this.webSocketIp = webSocketIp;
  }

  public int getWebSocketPort() {
    return webSocketPort;
  }

  public void setWebSocketPort(int webSocketPort) {
    this.webSocketPort = webSocketPort;
  }

  protected static class Serializer implements JsonSerializer<OscHostInfo> {
    @Override
    public JsonElement serialize(OscHostInfo src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty(NAME, src.name);
      obj.addProperty(OSC_IP, src.oscIp);
      obj.addProperty(OSC_PORT, src.oscPort);
      obj.addProperty(OSC_TRANSPORT, src.oscTransport);
      obj.addProperty(WS_IP, src.webSocketIp);
      obj.addProperty(WS_PORT, src.webSocketPort);
      return obj;
    }
  }
}
