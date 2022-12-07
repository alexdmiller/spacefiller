package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.Utils;
import spacefiller.math.Rnd;
import spacefiller.math.Vector;
import spacefiller.math.sdf.*;
import spacefiller.particles.Bounds;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.SPSystem;
import spacefiller.spaceplants.bees.BeeColor;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.bees.Hive;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.planets.PlanetSystem;
import spacefiller.spaceplants.plants.PlantDNA;
import spacefiller.spaceplants.plants.PlantSystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static spacefiller.math.sdf.FloatField2.sampleRandomPoint;

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
  private PlanetSystem planetSystem;
  private List<SPSystem> systems = new ArrayList<>();
  private int localFrameCount = 0;
  private int nextFrameId = 0;
  private int numHives;

  private FloatField2 repelField;

  @Override
  public void settings() {
    size(config.renderSize.width, config.renderSize.height, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
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
      canvas.noSmooth();
      canvas.hint(DISABLE_TEXTURE_MIPMAPS);
      ((PGraphicsOpenGL)canvas).textureSampling(3);
//
      finalRender = createGraphics(config.renderSize.width, config.renderSize.height, P2D);
      finalRender.noSmooth();
      finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
      ((PGraphicsOpenGL)finalRender).textureSampling(3);

      systems.clear();
      particleSystem = new ParticleSystem(new Bounds(canvas.width, canvas.height), config.maxParticles, 15);

      if (config.circleConstraints != null) {
        for (CircleConstraint constraint : config.circleConstraints) {
          particleSystem.addBehavior(
            new CircleBounds(
              (float) (constraint.radius + Math.random() * constraint.deviation - constraint.deviation / 2),
              1,
              0.1f,
              constraint.force),
            constraint.tag);
        }
      }

      particleSystem.addBehavior(new SoftBounds(10, 5, 3));

      if (config.planets != null) {
        Planets planets = config.planets;
        planetSystem = new PlanetSystem(
            particleSystem,
            planets.repelThreshold,
            planets.attractionThreshold,
            planets.noiseAmplitude,
            planets.noiseScale,
            planets.sdfSmooth);
        systems.add(planetSystem);
        int num = (int) (Math.random() * (planets.max - planets.min) + planets.min);
        for (int i = 0; i < num; i++) {
          planetSystem.createPlanet(
              (float) (Math.random() * (planets.maxRadius - planets.minRadius) + planets.minRadius));
        }

        repelField = new NoiseDistort(planetSystem.getRawSdf(), 100, 0.01f);
        repelField = new MapToRange(repelField, -100, 0, 5, 15);
      }

      if (config.dust != null) {
        dustSystem = new DustSystem(particleSystem);
        systems.add(dustSystem);

        if (repelField != null) {
          particleSystem.addBehavior(
              new RepelParticles(15, 0.5f, false), ParticleTag.DUST);
        } else {
          particleSystem.addBehavior(
              new RepelParticles(config.dust.repelThreshold, 0.1f, false), ParticleTag.DUST);
        }

        particleSystem.addBehavior(
            new RepelParticles(config.dust.repelHiveThreshold, 0.3f), ParticleTag.DUST, ParticleTag.HIVE);

        particleSystem.addBehavior(
            new RepelParticles(15, 1f), ParticleTag.DUST, ParticleTag.PLANT);

      }

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
          dna.setBranchThickness(plantConfig.branchThickness);

          plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2), dna);
        }
      }

      if (config.hives != null) {
        beeSystem = new BeeSystem(particleSystem, plantSystem, config.hives.globalRepelThreshold);
        if (config.hives.flocking != null) {
          Flocking flocking = config.hives.flocking;
          beeSystem.setFlockParameters(new FlockParticles.Parameters(
              flocking.separationWeight,
              flocking.alignmentWeight,
              flocking.cohesionWeight,
              flocking.alignmentThreshold ,
              flocking.cohesionThreshold ,
              flocking.desiredThreshold ,
              flocking.maxSpeed,
              flocking.maxForce
          ));
        }
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

        numHives = (int) Math.round(
            config.hives.min + Rnd.random.nextDouble() * (config.hives.max - config.hives.min));

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
    image(canvas, 0, 0, width, height);
  }

  private void stepSimulation() {
    // TODO: make sure this works when planets aren't present

    if (beeSystem.getHives().size() < numHives) {
      for (int i = 0; i < 1; i++) {
        int size = (int) (config.hives.hiveSize + Rnd.random.nextDouble() * config.hives.hiveSizeDeviation);
        Vector point = sampleRandomPoint(particleSystem.getBounds(), planetSystem.getSdf(), 0);
        // Vector point = particleSystem.getBounds().getRandomPointInside(2);
        if (point != null) {
          Hive hive = beeSystem.createHive(
              point, size, Math.random() < 0.1f);
          hive.setFlatteningForce(config.hives.hiveFlatteningForce);
          hive.setInnerRepelForce(config.hives.hiveInnerRepelForce);
          hive.setLineThickness(config.hives.lineThickness);
        }
      }
    }

    if (dustSystem.getNumDust() < config.dust.count) {
      for (int i = 0; i < 50; i++) {
        Vector point = sampleRandomPoint(particleSystem.getBounds(), planetSystem.getSdf(), 0);
        if (point != null) {
          dustSystem.createDust(point);
        }
      }
    }

    systems.forEach((system -> system.update()));
    particleSystem.update();
    if (planetSystem != null) {
      planetSystem.update();
    }
  }

  private void drawSimulation() {
    canvas.beginDraw();
    canvas.clear();

    if (config.backgroundOn) {
      canvas.background((int) config.backgroundColor);
    }

    if (planetSystem != null) {
      planetSystem.draw(canvas);
    }

//    debugDraw(repelField, 5, canvas, 15);

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

    finalRender.beginDraw();
    finalRender.clear();

    finalRender.image(canvas,
        0, 0,
        config.renderSize.width,
        config.renderSize.height);
    finalRender.blendMode(PConstants.BLEND);

    finalRender.endDraw();
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
