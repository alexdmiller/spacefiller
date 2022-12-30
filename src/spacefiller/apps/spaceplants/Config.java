package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.annotation.JsonProperty;
import spacefiller.particles.ParticleTag;

public class Config {
  @JsonProperty("on_frame")
  public RendererConfig onFrame;

  @JsonProperty("on_complete")
  public RendererConfig onComplete;

  @JsonProperty("max_frames")
  public int maxFrames = -1;

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

  @JsonProperty("render_preview")
  public boolean renderPreview = true;

  @JsonProperty("render_frames")
  public boolean renderFrames = false;

  @JsonProperty("render_frames_skip")
  public int renderFramesSkip = 1;

  @JsonProperty("planet_system")
  public Planets planetSystem;
}

class RendererConfig {
  @JsonProperty("show_preview")
  PreviewRendererConfig showPreview;

  @JsonProperty("save_png")
  SavePngRendererConfig savePng;

  @JsonProperty("save_svg")
  SaveSvgRendererConfig saveSvg;
}

class PreviewRendererConfig {
  @JsonProperty("max_width")
  int maxWidth;

  @JsonProperty("max_height")
  int maxHeight;

  @JsonProperty("background_color")
  long backgroundColor = 0x00000000;
}

class SavePngRendererConfig {
  @JsonProperty("scale")
  int scale;

  @JsonProperty("background_color")
  long backgroundColor = 0x00000000;

  @JsonProperty("background_on")
  boolean backgroundOn = false;

  public String filename;
}

class SaveSvgRendererConfig {
  @JsonProperty("scale")
  int scale;

  @JsonProperty("background_color")
  long backgroundColor = 0x00000000;

  @JsonProperty("background_on")
  boolean backgroundOn = false;

  String filename;
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
  @JsonProperty("branch_colors")
  public long[] branchColors = null;
  @JsonProperty("flower_color")
  public long aliveFlowerColor = 0xfff5198b;
  @JsonProperty("flower_colors")
  public long[] flowerColors = null;
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
  @JsonProperty("flower_deviation")
  public float flowerDeviation = 0;
  @JsonProperty("mass")
  public float mass = 1;
  @JsonProperty("branch_thickness")
  public float branchThickness = 2;
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
  @JsonProperty("hive_size_deviation")
  public int hiveSizeDeviation = 0;
  @JsonProperty("hive_flattening_force")
  public float hiveFlatteningForce = 0;
  @JsonProperty("hive_inner_repel_force")
  public float hiveInnerRepelForce = 0.02f;
  @JsonProperty("global_repel_threshold")
  public float globalRepelThreshold = 20;
  @JsonProperty("spikes")
  public boolean spikes = true;
  @JsonProperty("colors")
  public HiveColor[] colors;
  @JsonProperty("line_thickness")
  public float lineThickness = 2;
  public Flocking flocking = new Flocking();
}

class HiveColor {
  @JsonProperty("hive_color")
  public long hiveColor;
  @JsonProperty("bee_color")
  public long beeColor;
}

class Flocking {
  @JsonProperty("separation_weight")
  public float separationWeight = 1;
  @JsonProperty("alignment_weight")
  public float alignmentWeight = 2;
  @JsonProperty("cohesion_weight")
  public float cohesionWeight = 1;
  @JsonProperty("alignment_threshold")
  public float alignmentThreshold = 20;
  @JsonProperty("cohesion_threshold")
  public float cohesionThreshold = 20;
  @JsonProperty("desired_threshold")
  public float desiredThreshold = 17;
  @JsonProperty("max_speed")
  public float maxSpeed = 2f;
  @JsonProperty("max_force")
  public float maxForce = 0.1f;
}

class Dust {
  public int count;

  @JsonProperty("repel_threshold")
  public float repelThreshold = 10;

  @JsonProperty("repel_hive_threshold")
  public float repelHiveThreshold = 10;
}

class CircleConstraint {
  @JsonProperty
  public float radius;

  @JsonProperty
  public float deviation;

  @JsonProperty
  public ParticleTag tag;

  @JsonProperty
  public float force = 0.01f;
}

class Planets {
  @JsonProperty
  public PlanetConfig[] planets;

  @JsonProperty("repel_threshold")
  public float repelThreshold = 20;

  @JsonProperty("attraction_threshold")
  public float attractionThreshold = 400;

  @JsonProperty("noise_amplitude")
  public float noiseAmplitude = 0;

  @JsonProperty("noise_scale")
  public float noiseScale = 0;

  @JsonProperty("sdf_smooth")
  public float sdfSmooth = 0.1f;
}

class PlanetConfig {
  public ParticleTag[] tags;

  @JsonProperty("min_radius")
  public float minRadius = 10;
  @JsonProperty("max_radius")
  public float maxRadius = 1000;
}