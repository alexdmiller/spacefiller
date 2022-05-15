package spacefiller.patchbay.oscquery;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import static spacefiller.patchbay.oscquery.Constants.*;

public class OscRange {
  private float min;
  private float max;

  public OscRange(float min, float max) {
    this.min = min;
    this.max = max;
  }

  public float getMin() {
    return min;
  }

  public float getMax() {
    return max;
  }

  protected static class Serializer implements JsonSerializer<OscRange> {
    @Override
    public JsonElement serialize(OscRange src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty(MIN, src.min);
      obj.addProperty(MAX, src.max);
      return obj;
    }
  }
}
