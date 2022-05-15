package spacefiller.patchbay.oscquery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import spacefiller.patchbay.osc.OscType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static spacefiller.patchbay.oscquery.Constants.*;

public class OscNode {
  private String fullPath;
  private String description;
  private OscType[] types;
  private Object[] values;
  private OscRange[] ranges;
  private Map<String, OscNode> contents;

  public OscNode(String fullPath, String description) {
    this.fullPath = fullPath;
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public String getFullPath() {
    return fullPath;
  }

  public OscType[] getTypes() {
    return types;
  }

  public void setTypes(OscType... types) {
    this.types = types;
  }

  public Object[] getValues() {
    return values;
  }

  public void setValues(Object... values) {
    this.values = values;
  }

  public OscRange[] getRanges() {
    return ranges;
  }

  public void setRanges(OscRange... ranges) {
    this.ranges = ranges;
  }

  public Map<String, OscNode> getContents() {
    return contents;
  }

  protected void put(String path, OscNode node) {
    if (contents == null) {
      contents = new HashMap<>();
    }

    int nextSlash = path.indexOf('/', 1);

    if (nextSlash == -1) {
      // Remove leading slash
      contents.put(path.substring(1), node);
    } else {
      String head = path.substring(1, nextSlash);
      String rest = path.substring(nextSlash);
      if (!contents.containsKey(head)) {
        String prev = getFullPath();

        contents.put(
            head,
            new OscNode(
                getFullPath() + (prev.endsWith("/") ? "" : "/") + head,
                ""));
      }

      contents.get(head).put(rest, node);
    }
  }

  protected static class Serializer implements JsonSerializer<OscNode> {
    @Override
    public JsonElement serialize(OscNode src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.add(FULL_PATH, context.serialize(src.getFullPath()));
      obj.add(DESCRIPTION, context.serialize(src.getDescription()));
      obj.add(CONTENTS, context.serialize(src.contents));
      // TODO: Support different access types
      obj.addProperty(ACCESS, "3");

      String typeString = "";
      if (src.types != null) {
        StringBuilder builder = new StringBuilder();
        for (OscType type : src.types) {
          builder.append(type);
        }
        typeString = builder.toString();
      }

      obj.addProperty(TYPES, typeString);
      obj.add(VALUES, context.serialize(src.values));
      obj.add(RANGE, context.serialize(src.ranges));

      return obj;
    }
  }
}
