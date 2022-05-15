package spacefiller.patchbay.osc;

import oscP5.OscMessage;
import spacefiller.patchbay.signal.Node;

public interface OscParser {
  void parse(OscMessage message, Node target);
}
