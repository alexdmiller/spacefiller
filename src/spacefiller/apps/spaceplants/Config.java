package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.annotation.JsonProperty;
import spacefiller.math.Rnd;
import spacefiller.particles.ParticleTag;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;

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

  @JsonProperty
  public PlantConfig[] types;
}

class PlantConfig {
  @JsonProperty("branch_color")
  public long aliveBranchColor = 0xff53982D;
  @JsonProperty("flower_color")
  public long aliveFlowerColor = 0xfff5198b;
  @JsonProperty("connection_length")
  public float connectionLength = 10;
  @JsonProperty("branching_factor")
  public long branchingFactor = 7;
  @JsonProperty("branching_falloff")
  public float branchingFactorFalloff = 0;
  @JsonProperty("seed_branching_factor")
  public long seedBranchingFactor = 4;
  @JsonProperty("branching_factor_deviation")
  public long branchingFactorDeviation = 0;
  @JsonProperty("max_depth")
  public long maxDepth = 4;
  @JsonProperty("max_depth_deviation")
  public long maxDepthDeviation = 0;
  @JsonProperty("branch_chance")
  public float branchChance = 0.5f;
  @JsonProperty("flower_size")
  public float flowerSize = 4;
}

class Hives {
  @JsonProperty
  public int min;
  @JsonProperty
  public int max;
  @JsonProperty("bees_per_hive")
  public int beesPerHive = 10;
  @JsonProperty("initial_bees_per_hive")
  public int startingBeesPerHive = 10;
  @JsonProperty("hive_size")
  public int hiveSize = 10;
  @JsonProperty("colors")
  public HiveColor[] colors;
}

class HiveColor {
  @JsonProperty("hive_color")
  public long hiveColor;
  @JsonProperty("bee_color")
  public long beeColor;
}

class Dust {
  public int count;
}

class CircleConstraint {
  @JsonProperty
  public float radius;

  @JsonProperty
  public float deviation;

  @JsonProperty
  public ParticleTag tag;
}

