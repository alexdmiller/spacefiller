package spacefiller.patchbay.oscquery;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import static spacefiller.patchbay.oscquery.Constants.HOST_INFO;

public class OscQueryServer extends OscNode {
  // TODO: figure out a better way to choose a port
  private static int DEFAULT_PORT = 8000;
  private OscHostInfo hostInfo;
  private Gson gson;
  private HttpServer server;
  private JmDNS jmdns;
  private ServiceInfo serviceInfo;

  public OscQueryServer(String name, String oscIp, int oscPort) {
    this(DEFAULT_PORT, name, oscIp, oscPort);
  }

  public OscQueryServer(int port, String name, String oscIp, int oscPort) {
    super("/", "");

    new RegisterJmDNS(port).start();

    // TODO: make sure this host info is correct (transport? websocket?)
    hostInfo = new OscHostInfo(name, oscIp, oscPort, "UDP", "", 0);

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(OscNode.class, new OscNode.Serializer());
    builder.registerTypeAdapter(OscQueryServer.class, new OscNode.Serializer());
    builder.registerTypeAdapter(OscRange.class, new OscRange.Serializer());
    builder.registerTypeAdapter(OscHostInfo.class, new OscHostInfo.Serializer());
    gson = builder.create();

    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
    } catch (BindException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    server.createContext("/", new Handler(this));
    server.setExecutor(null); // creates a default executor
    server.start();
  }

  protected class RegisterJmDNS extends Thread {
    int port;

    public RegisterJmDNS(int port) {
      this.port = port;
    }

    public void run(){
      try {
        jmdns = JmDNS.create(InetAddress.getLocalHost());
        serviceInfo = ServiceInfo.create("_oscjson._tcp.local.", "patchbay", port, "path=/");
        jmdns.registerService(serviceInfo);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  protected class UnregisterJmDNS extends Thread {
    public void run(){
      try {
        jmdns.unregisterService(serviceInfo);
        jmdns.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void put(OscNode node) {
    // skip first "/" character
    put(node.getFullPath(), node);
  }

  public void stop() {
    if (server != null) {
      server.stop(0);
      new UnregisterJmDNS().start();
    }
  }

  protected class Handler implements HttpHandler {
    private OscNode container;

    public Handler(OscNode container) {
      this.container = container;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
      String query = t.getRequestURI().getQuery();
      if (query != null && query.equals(HOST_INFO)) {
        response(t, hostInfo);
      } else {
        String path = t.getRequestURI().getPath();
        String[] parts = path.split("/");
        OscNode node = container;
        // Walk the tree down to the requested node
        for (String part : parts) {
          if (!part.isEmpty()) {
            node = node.getContents().get(part);
          }
        }
        response(t, node);
      }
    }

    private void response(HttpExchange t, Object object) throws IOException {
      String response = gson.toJson(object);
      t.getResponseHeaders().add("Content-Type", "application/json");
      t.sendResponseHeaders(200, response.length());
      OutputStream os = t.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
  }
}