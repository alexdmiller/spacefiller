package spacefiller.apps.patchbay;

import spacefiller.patchbay.osc.OscType;
import spacefiller.patchbay.oscquery.OscNode;
import spacefiller.patchbay.oscquery.OscQueryServer;

import java.io.IOException;

// TODO: why does OSCQueryHelper not have server name?
// TODO: construct full path recursively for nodes

public class OscQueryServerExample {
  public static void main(String[] args) throws InterruptedException, IOException {
    OscQueryServer server = new OscQueryServer("OscQueryServerExample", "localhost", 9999);

    OscNode method1 = new OscNode(
        "/my_method",
        "this is an example method");
    method1.setTypes(OscType.BOOLEAN);
    method1.setValues(false);

    OscNode method2 = new OscNode(
        "/my/nested/method",
        "this is an example nested method");

    OscNode method3 = new OscNode(
        "/my/nested/another_method",
        "this is an example nested method");

    server.put(method1);
    server.put(method2);
    server.put(method3);

    Thread.sleep(10000);
    server.stop();
  }
}