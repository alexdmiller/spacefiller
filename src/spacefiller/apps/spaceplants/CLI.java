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
import spacefiller.particles.Bounds;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.System;
import spacefiller.spaceplants.bees.BeeColor;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.planets.Planet;
import spacefiller.spaceplants.plants.PlantDNA;
import spacefiller.spaceplants.plants.PlantSystem;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CLI extends PApplet {
  private static String outputPath;
  private static String configPath;
  private static Config config;
  private static boolean renderPreview = true;

  public static void main(String[] args) {
    outputPath = args[0];
    configPath = args[1];
    Rnd.init(args.length == 3 ? new Random(Long.valueOf(args[2])) : new Random());

    renderPreview = java.lang.System.getenv().containsKey("RENDER_PREVIEW");

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

  @Override
  public void settings() {
    size(config.renderSize.width, config.renderSize.height, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    Utils.init(this);
    noSmooth();


    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);

    frameRate(60);

    setupSimulation();
    drawSimulation();

    if (!renderPreview) {
      for (int i = 0; i < config.maxFrames; i++) {
        stepSimulation();
        if (i % 100 == 0) {
          java.lang.System.out.println("Computed " + i + " / " + config.maxFrames + " steps");
        }
      }
      drawSimulation();
      renderLarge();
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

      finalRender = createGraphics(config.renderSize.width, config.renderSize.height, P2D);
      finalRender.noSmooth();
      finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
      ((PGraphicsOpenGL)finalRender).textureSampling(3);

      systems.clear();
      particleSystem = new ParticleSystem(new Bounds(canvas.width, canvas.height), config.maxParticles, 15);

      if (config.dust != null) {
        dustSystem = new DustSystem(particleSystem, config.dust.count, canvas.width, canvas.height);
        systems.add(dustSystem);
      }

      RepelParticles repelDust = new RepelParticles(10, 0.1f);
      particleSystem.addBehavior(repelDust, ParticleTag.DUST);

      RepelParticles repelBees = new RepelParticles(10, 0.1f);
      particleSystem.addBehavior(repelBees, ParticleTag.BEE);

      if (config.circleConstraints != null) {
        for (CircleConstraint constraint : config.circleConstraints) {
          particleSystem.addBehavior(new CircleBounds(
              (float) (constraint.radius + Math.random() * constraint.deviation - constraint.deviation / 2),
              1, 0.1f), constraint.tag);
        }
      }

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
        beeSystem = new BeeSystem(particleSystem, plantSystem);
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
          beeSystem.createHive(particleSystem.getBounds().getRandomPointInside(2), size);
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
    if (renderPreview) {
      if (localFrameCount < config.maxFrames) {
        for (int i = 0; i < 10; i++) {
          stepSimulation();
          localFrameCount++;
        }
        drawSimulation();
      }
      image(canvas, 0, 0, width, height);
    }
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

    canvas.noStroke();
    canvas.fill(255);

    beeSystem.setLightLevel(1);
    plantSystem.setLightLevel(1);

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

  private void renderLarge() {
    java.lang.System.out.println("Rendering image to " + outputPath);

    finalRender.clear();

    finalRender.image(canvas,
        0, 0,
        config.renderSize.width,
        config.renderSize.height);
    finalRender.blendMode(PConstants.BLEND);

    java.lang.System.out.println("Saving...");
    finalRender.save(outputPath);
  }

  @Override
  public void keyPressed() {
    if (key == 'r' && renderPreview) {
      setupSimulation();
      localFrameCount = 0;
    }
  }
}
