package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.Utils;
import spacefiller.math.sdf.*;
import spacefiller.math.Rnd;
import spacefiller.particles.Bounds;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.System;
import spacefiller.spaceplants.bees.BeeColor;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.bees.Hive;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.plants.PlantDNA;
import spacefiller.spaceplants.plants.PlantSystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static spacefiller.math.sdf.FieldVisualizer.drawField;

public class CLI extends PApplet {
  private static String outputPath;
  private static String configPath;
  private static Config config;

  public static void main(String[] args) {
    outputPath = args[0];
    configPath = args[1];
    Rnd.init(args.length == 3 ? new Random(Long.valueOf(args[2])) : new Random());

    try {
      readConfig();
      PApplet.main("spacefiller.apps.spaceplants.CLI");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void readConfig() throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    config = mapper.readValue(Paths.get(configPath).toFile(), Config.class);
  }

  private ParticleSystem particleSystem;
  private PGraphics canvas;
  private PGraphics finalRender;
  private PlantSystem plantSystem;
  private SymmetricRepel symmetricRepel;
  private DustSystem dustSystem;
  private BeeSystem beeSystem;
  private List<System> systems = new ArrayList<>();
  private int localFrameCount = 0;
  private int nextFrameId = 0;

  private FloatField2 field = (x, y) -> 0.5f;

  private FloatField2 circle;
  private FloatField2 plantCircleField;

  @Override
  public void settings() {
    size(config.renderSize.width, config.renderSize.height, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    FloatField2[] planets = new FloatField2[10];
    for (int i = 0; i < 10; i++) {
      planets[i] = new Circle(
          (float) (config.simSize.width * Math.random()),
          (float) (config.simSize.height * Math.random()),
          (float) (Math.random() * 150 + 10));
    }
    circle = new Normalize(
        config.simSize.width,
        config.simSize.height,
        20,
        new Floor(new NoiseDistort(new Union(planets))));


    FloatField2[] plantCircles = new FloatField2[10];
    for (int i = 0; i < 10; i++) {
      plantCircles[i] = new Circle(
          (float) (config.simSize.width * Math.random()),
          (float) (config.simSize.height * Math.random()),
          (float) (Math.random() * 150 + 10));
    }
    plantCircleField = new Normalize(
        config.simSize.width,
        config.simSize.height,
        20,
        new Floor(new NoiseDistort(new Union(plantCircles))));


    Utils.init(this);
    // noSmooth();


    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);

    frameRate(60);

    setupSimulation();
    drawSimulation();

    if (!config.renderPreview) {
      for (int i = 0; i < config.maxFrames; i++) {
        stepSimulation();
        if (config.renderFrames && (i % config.renderFramesSkip) == 0) {
          drawSimulation();
          saveLargeFrame(outputPath + "/" + String.format("%04d", nextFrameId) + ".tif");
          nextFrameId++;
        }
        if (i % 100 == 0) {
          java.lang.System.out.println("Computed " + i + " / " + config.maxFrames + " steps");
        }
      }
      drawSimulation();
      saveLargeFrame(outputPath);
      exit();
    }
  }

  private void setupSimulation() {
    try {
      readConfig();

      canvas = createGraphics(config.simSize.width, config.simSize.height, P2D);
//      canvas.noSmooth();
//      canvas.hint(DISABLE_TEXTURE_MIPMAPS);
//      ((PGraphicsOpenGL)canvas).textureSampling(3);
//
      finalRender = createGraphics(config.renderSize.width, config.renderSize.height, P2D);
//      finalRender.noSmooth();
//      finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
//      ((PGraphicsOpenGL)finalRender).textureSampling(3);

      systems.clear();
      particleSystem = new ParticleSystem(new Bounds(canvas.width, canvas.height), config.maxParticles, 15);

      if (config.dust != null) {
        dustSystem = new DustSystem(particleSystem, config.dust.count, canvas.width, canvas.height);
        systems.add(dustSystem);
      }

      RepelParticles repelDust = new RepelParticles(5, 0.1f);
      particleSystem.addBehavior(repelDust, ParticleTag.DUST);

      RepelParticles repelBees = new RepelParticles(10, 0.1f);
      particleSystem.addBehavior(repelBees, ParticleTag.BEE);

//      if (config.circleConstraints != null) {
//        for (CircleConstraint constraint : config.circleConstraints) {
//          particleSystem.addBehavior(
//            new CircleBounds(
//              (float) (constraint.radius + Math.random() * constraint.deviation - constraint.deviation / 2),
//              1,
//              0.1f,
//              constraint.force),
//            constraint.tag);
//        }
//      }

      particleSystem.addBehavior(new FollowGradient(circle, 0.5f, true), ParticleTag.DUST);
      particleSystem.addBehavior(new FollowGradient(circle, 0.5f, true), ParticleTag.HIVE);
      particleSystem.addBehavior(new FollowGradient(plantCircleField, 0.5f, true), ParticleTag.PLANT);


      particleSystem.addBehavior(new SoftBounds(10, 5, 3));

      if (config.plants != null) {
        plantSystem = new PlantSystem(particleSystem);
        systems.add(plantSystem);

        int numPlants = (int) Math.round(
            config.plants.min + Rnd.random.nextDouble() * (config.plants.max - config.plants.min));

        List<PlantConfig> plantConfigs = Arrays.asList(config.plants.types);
        Collections.shuffle(plantConfigs);

        for (int i = 0; i < numPlants; i++) {
          PlantConfig plantConfig = plantConfigs.get(i % plantConfigs.size());
          PlantDNA dna = new PlantDNA();

          if (plantConfig.branchColors != null) {
            int randomColorIndex = (int) (Math.random() * plantConfig.branchColors.length);
            dna.setAliveBranchColor((int) plantConfig.branchColors[randomColorIndex]);
          } else {
            dna.setAliveBranchColor((int) plantConfig.aliveBranchColor);
          }

          if (plantConfig.flowerColors != null) {
            int randomColorIndex = (int) (Math.random() * plantConfig.flowerColors.length);
            dna.setAliveFlowerColor((int) plantConfig.flowerColors[randomColorIndex]);
          } else {
            dna.setAliveFlowerColor((int) plantConfig.aliveFlowerColor);
          }

          dna.setBranchChance(plantConfig.branchChance);
          dna.setBranchingFactor((int) plantConfig.branchingFactor);
          dna.setBranchingFactorDeviation((int) plantConfig.branchingFactorDeviation);
          dna.setBranchingFactorFalloff(plantConfig.branchingFactorFalloff);
          dna.setConnectionLength((int) plantConfig.connectionLength);

          dna.setFlowerSize((float) (plantConfig.flowerSize + Math.random() * plantConfig.flowerDeviation));
          dna.setMaxDepth((int) plantConfig.maxDepth);
          dna.setMaxDepthDeviation((int) plantConfig.maxDepthDeviation);
          dna.setSeedBranchingFactor((int) plantConfig.seedBranchingFactor);
          dna.setMass(plantConfig.mass);

          plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2), dna);
        }
      }

      if (config.hives != null) {
        beeSystem = new BeeSystem(particleSystem, plantSystem, config.hives.globalRepelThreshold);
        systems.add(beeSystem);
        Params.set(PName.MAX_BEES_CREATED, config.hives.beesPerHive);
        Params.set(PName.STARTING_BABIES_PER_HIVE, config.hives.startingBeesPerHive);
        Params.set(PName.MAX_HIVE_SIZE, config.hives.hiveSize);

        if (config.hives.colors != null) {
          List<BeeColor> colors = Arrays.stream(config.hives.colors)
              .map(c -> new BeeColor(
                  (int) c.hiveColor, (int) c.hiveColor, (int) c.beeColor, (int) c.beeColor))
              .collect(Collectors.toList());
          beeSystem.setColors(colors.toArray(new BeeColor[colors.size()]));
        }

        int numHives = (int) Math.round(
            config.hives.min + Rnd.random.nextDouble() * (config.hives.max - config.hives.min));
        for (int i = 0; i < numHives; i++) {
          int size = (int) (config.hives.hiveSize + Rnd.random.nextDouble() * config.hives.hiveSizeDeviation);
          Hive hive = beeSystem.createHive(
              particleSystem.getBounds().getRandomPointInside(2), size, config.hives.spikes);
          hive.setFlatteningForce(config.hives.hiveFlatteningForce);
          hive.setInnerRepelForce(config.hives.hiveInnerRepelForce);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      exit();
    }
  }

  @Override
  public void draw() {
    clear();
    if (config.maxFrames == -1 || localFrameCount < config.maxFrames) {
      java.lang.System.out.println(localFrameCount + " % " + config.renderFramesSkip);
      if (config.renderFrames) {
        stepSimulation();
        if (localFrameCount % 100 == 0) {
          java.lang.System.out.println("Computed " + localFrameCount + " / " + config.maxFrames + " steps");
        }
        localFrameCount++;
        if (localFrameCount % config.renderFramesSkip == 0) {
          drawSimulation();
          saveLargeFrame(outputPath + "/" + String.format("%04d", nextFrameId) + ".tif");
          nextFrameId++;
        }
      } else {
        for (int i = 0; i < 10; i++) {
          stepSimulation();
          if (localFrameCount % 100 == 0) {
            java.lang.System.out.println("Computed " + localFrameCount + " / " + config.maxFrames + " steps");
          }
          localFrameCount++;
        }
        drawSimulation();
      }
    }
    image(canvas, 0, 0, canvas.width, canvas.height);
  }

  private void stepSimulation() {
    systems.forEach((system -> system.update()));
    particleSystem.update();
  }

  private void drawSimulation() {
    canvas.beginDraw();
    canvas.clear();

    if (config.backgroundOn) {
      canvas.background((int) config.backgroundColor);
    }

    // drawField(circle, canvas, 5, 0, 1);

    canvas.noStroke();
    canvas.fill(255);

    if (beeSystem != null) {
      beeSystem.setLightLevel(1);
    }

    if (plantSystem != null) {
      plantSystem.setLightLevel(1);
    }

    systems.forEach((system -> system.draw(canvas)));

    canvas.endDraw();

//    finalRender.beginDraw();
//    finalRender.clear();
//
//    finalRender.image(canvas,
//        0, 0,
//        config.renderSize.width,
//        config.renderSize.height);
//    finalRender.blendMode(PConstants.BLEND);
//
//    finalRender.endDraw();
  }

  private void saveLargeFrame(String filename) {
    java.lang.System.out.println("Saving " + filename);
    finalRender.clear();

    finalRender.image(canvas,
        0, 0,
        config.renderSize.width,
        config.renderSize.height);
    finalRender.blendMode(PConstants.BLEND);

    finalRender.save(filename);
  }

  @Override
  public void keyPressed() {
    if (key == 'r' && config.renderPreview) {
      setupSimulation();
      localFrameCount = 0;
    }
  }
}
