package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
  @JsonProperty("max_frames")
  public int maxFrames = 100;

  @JsonProperty("max_particles")
  public int maxParticles = 1000;

  @JsonProperty("sim_size")
  public Size simSize;

  @JsonProperty("render_size")
  public Size renderSize;

  @JsonProperty("plants")
  public Plants plants;

  @JsonProperty("dust")
  public Dust dust;

  @JsonProperty("circle_constraint")
  public CircleConstraint circleConstraint;
}

class Size {
  public int width;
  public int height;

  @Override
  public String toString() {
    return "Size{" +
        "width=" + width +
        ", height=" + height +
        '}';
  }
}

class Plants {
  @JsonProperty
  public int min;
  @JsonProperty
  public int max;
}

class Dust {
  public int count;
}

class CircleConstraint {
  @JsonProperty
  public float radius;
}

