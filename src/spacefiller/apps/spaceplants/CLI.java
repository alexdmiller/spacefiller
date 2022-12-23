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

    // TODO: Figure out why this doesn't work
    // Rnd.init(args.length == 3 ? new Random(Long.valueOf(args[2])) : new Random());
    Rnd.init(new Random());

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
  private PlanetSystem metaPlanetSystem;
  private List<SPSystem> systems = new ArrayList<>();
  private int localFrameCount = 0;
  private int nextFrameId = 0;
  private int numHives;
  private boolean setup;

  private FloatField2 repelField;

  @Override
  public void settings() {
    size(config.renderSize.width, config.renderSize.height, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    Utils.init(this);

    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);

    frameRate(60);
  }

  private void doSetup() {
    setupSimulation();
//    drawSimulation();
    if (!config.renderPreview) {
      for (int i = 0; i < config.maxFrames; i++) {
        stepSimulation();
        if (config.renderFrames && (i % config.renderFramesSkip) == 0) {
//          drawSimulation();
//          saveLargeFrame(outputPath + "/" + String.format("%04d", nextFrameId) + ".tif");
          nextFrameId++;
        }
        if (i % 100 == 0) {
          java.lang.System.out.println("Computed " + i + " / " + config.maxFrames + " steps");
        }
        localFrameCount++;
      }
      drawSimulation();
//      saveLargeFrame(outputPath);
      exit();
    }
    setup = true;
  }

  private void setupSimulation() {
    try {
      readConfig();

      canvas = createGraphics(
          config.simSize.width,
          config.simSize.height,
          SVG,
          "/Users/alex/projects/spacefiller/output.svg");

//      canvas = createGraphics(config.simSize.width, config.simSize.height, P2D);
//      canvas.noSmooth();
//      canvas.hint(DISABLE_TEXTURE_MIPMAPS);
//      ((PGraphicsOpenGL)canvas).textureSampling(3);


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

      particleSystem.addBehavior(new SoftBounds(1, 5, 3));

      if (config.planetSystem != null) {
        Planets planets = config.planetSystem;
        planetSystem = new PlanetSystem(
            particleSystem,
            planets.repelThreshold,
            planets.attractionThreshold,
            planets.noiseAmplitude,
            planets.noiseScale,
            planets.sdfSmooth);
        planetSystem.getParticleSystem().setDebugColor(0xffffffff);
        systems.add(planetSystem);

        for (PlanetConfig config : planets.planets) {
          planetSystem.createPlanet(
              (float) (Math.random() * (config.maxRadius - config.minRadius) + config.minRadius),
              config.tags);
        }

        metaPlanetSystem = new PlanetSystem(
            planetSystem.getParticleSystem(),
            0,
            planets.attractionThreshold,
            planets.noiseAmplitude,
            planets.noiseScale,
            0.11f);
        metaPlanetSystem.getParticleSystem().setDebugColor(0xffff0000);
        systems.add(metaPlanetSystem);
        metaPlanetSystem.createPlanet(400, new ParticleTag[]{ParticleTag.PLANET});
        metaPlanetSystem.createPlanet(400, new ParticleTag[]{ParticleTag.PLANET});
        metaPlanetSystem.createPlanet(200, new ParticleTag[]{ParticleTag.PLANET});
        metaPlanetSystem.createPlanet(200, new ParticleTag[]{ParticleTag.PLANET});
      }

      if (config.dust != null) {
        dustSystem = new DustSystem(particleSystem);
        systems.add(dustSystem);

        particleSystem.addBehavior(
            new RepelParticles(config.dust.repelThreshold, 0.1f, false), ParticleTag.DUST);

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
          beeSystem.setColors(Arrays.stream(config.hives.colors)
              .map(c -> new BeeColor(
                  (int) c.hiveColor, (int) c.hiveColor, (int) c.beeColor, (int) c.beeColor)).toArray(BeeColor[]::new));
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
    if (!setup) {
      doSetup();
    }

//    clear();
//    if (config.maxFrames == -1 || localFrameCount < config.maxFrames) {
//      java.lang.System.out.println(localFrameCount + " % " + config.renderFramesSkip);
//      if (config.renderFrames) {
//        stepSimulation();
//        if (localFrameCount % 100 == 0) {
//          java.lang.System.out.println("Computed " + localFrameCount + " / " + config.maxFrames + " steps");
//        }
//        localFrameCount++;
//        if (localFrameCount % config.renderFramesSkip == 0) {
//          drawSimulation();
//          saveLargeFrame(outputPath + "/" + String.format("%04d", nextFrameId) + ".tif");
//          nextFrameId++;
//        }
//      } else {
//        for (int i = 0; i < 10; i++) {
//          stepSimulation();
//          if (localFrameCount % 100 == 0) {
//            java.lang.System.out.println("Computed " + localFrameCount + " / " + config.maxFrames + " steps");
//          }
//          localFrameCount++;
//        }
//        drawSimulation();
//      }
//    }
//    image(canvas, 0, 0, width, height);
  }

  private Vector sampleRandomPoint(Bounds bounds, FloatField2 field, float threshold) {
    for (int i = 0; i < 10; i++) {
      Vector candidate = FloatField2.sampleRandomPoint(bounds, field, threshold);
      if (candidate != null && particleSystem.getDensity(candidate) < 0.05) {
        return candidate;
      }
    }
    return null;
  }

  private void stepSimulation() {
    int threshold = planetSystem == null ? 0 : 500;
    if (localFrameCount > threshold) {
      if (beeSystem.getHives().size() < numHives) {
        for (int i = 0; i < 1; i++) {
          int size = (int) (config.hives.hiveSize + Rnd.random.nextDouble() * config.hives.hiveSizeDeviation);
          ParticleTag type = ParticleTag.HIVE_LIGHT;
          if (Math.random() < 0.5) {
            type = ParticleTag.HIVE_DARK;
          }
          Vector point = planetSystem == null
              ? particleSystem.getBounds().getRandomPointInside(2)
              : sampleRandomPoint(particleSystem.getBounds(), planetSystem.getSdf(type), 0);
          if (point != null) {
            Hive hive = beeSystem.createHive(
                point, size, Math.random() < 0.1f, type);
            hive.setFlatteningForce(config.hives.hiveFlatteningForce);
            hive.setInnerRepelForce(config.hives.hiveInnerRepelForce);
            hive.setLineThickness(config.hives.lineThickness);
          }
        }
      }

      if (dustSystem.getNumDust() < config.dust.count) {
        for (int i = 0; i < 50; i++) {
          Vector point = planetSystem == null
              ? particleSystem.getBounds().getRandomPointInside(2)
              : sampleRandomPoint(particleSystem.getBounds(), planetSystem.getSdf(ParticleTag.DUST), 0);
          if (point != null) {
            dustSystem.createDust(point);
          }
        }
      }
    }


    systems.forEach((SPSystem::update));
    particleSystem.update();
  }

  private void drawSimulation() {
    canvas.beginDraw();
//    canvas.clear();

    if (config.backgroundOn) {
      canvas.background((int) config.backgroundColor);
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
    canvas.dispose();
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
