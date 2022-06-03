package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.annotation.JsonProperty;
import spacefiller.particles.ParticleTag;

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

  @JsonProperty("hives")
  public Hives hives;

  @JsonProperty("circle_constraints")
  public CircleConstraint[] circleConstraints;
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

class Hives {
  @JsonProperty
  public int min;
  @JsonProperty
  public int max;
  @JsonProperty("bees_per_hive")
  public int beesPerHive;
}

class Dust {
  public int count;
}

class CircleConstraint {
  @JsonProperty
  public float radius;

  @JsonProperty
  public ParticleTag tag;
}

