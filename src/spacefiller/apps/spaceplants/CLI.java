/*
TODO: add planet renderer ?
TODO: experiment with planet only simulation to get the shape of planets & meta planets right
TODO: add more density
*/

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
import spacefiller.spaceplants.planets.Planet;
import spacefiller.spaceplants.planets.PlanetSystem;
import spacefiller.spaceplants.plants.PlantDNA;
import spacefiller.spaceplants.plants.PlantSystem;
import spacefiller.spaceplants.rendering.PreviewRenderer;
import spacefiller.spaceplants.rendering.RasterExportRenderer;
import spacefiller.spaceplants.rendering.Renderer;
import spacefiller.spaceplants.rendering.SvgExportRenderer;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

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
  private PlantSystem plantSystem;
  private DustSystem dustSystem;
  private BeeSystem beeSystem;
  private PlanetSystem planetSystem;
  private List<SPSystem> systems = new ArrayList<>();
  private int localFrameCount = 0;
  private int numHives;
  private boolean setup;


  private List<Renderer> onFrameRenderers;
  private List<Renderer> onCompleteRenderers;

  @Override
  public void settings() {
    System.out.println("Settings");
    Size simSize = config.simSize;
    if (config.onFrame != null && config.onFrame.showPreview != null) {
      ShowPreview preview = config.onFrame.showPreview;

      int width = 0;
      int height = 0;
      if (simSize.width > simSize.height) {
        width = preview.maxWidth;
        height = Math.round((float) simSize.height / simSize.width * width);
      } else {
        height = preview.maxHeight;
        width = Math.round((float) simSize.width / simSize.height * height);
      }

      size(width, height, P2D);
    } else {
      size(simSize.width, simSize.height, P2D);
    }
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    System.out.println("Initializing utils");
    Utils.init(this);

    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);

    System.out.println("Setting framerate");
    frameRate(60);
  }

  private void doSetup() {
    System.out.println("Running setup");
    setupSimulation();
    setup = true;
  }

  private void setupSimulation() {
    try {
      System.out.println("Reading configuration");
      readConfig();

      systems.clear();

      System.out.println("Creating particle system");
      particleSystem = new ParticleSystem(new Bounds(config.simSize.width, config.simSize.height), config.maxParticles, 15);

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

      particleSystem.addBehavior(new SoftBounds(32, 5, 3));

      System.out.println("Initializing planet system");
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
          for (int i = 0; i < config.count; i++) {
            planetSystem.createPlanet(
                (float) (Math.random() * (config.maxRadius - config.minRadius) + config.minRadius),
                config.tags);
          }
        }
        planetSystem.recomputeSdf();
      }

      System.out.println("Initializing dust");
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

      System.out.println("Initializing plants");
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

      System.out.println("Initializing hives");
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

      systems.add(new SPSystem(){
        @Override
        public void update() {

        }

        @Override
        public void draw(PGraphics graphics) {
          graphics.noFill();
          graphics.rectMode(PConstants.CORNERS);

          int borderSize = 16;

          graphics.stroke(0);
          graphics.strokeWeight(borderSize);
          graphics.rect(borderSize/2, borderSize/2, graphics.width - borderSize/2, graphics.height - borderSize/2);

          graphics.strokeWeight(1);
          graphics.stroke(255);
          graphics.rect(16, 16, graphics.width - 16, graphics.height - 16);

          graphics.strokeWeight(1);
          graphics.stroke(100);

          for (int x = borderSize; x <= graphics.width - borderSize; x += borderSize) {
            int lineSize = borderSize/6;
            if (x % (borderSize * 4) == borderSize) {
              lineSize = borderSize/2;
            }
            graphics.line(x, borderSize/2 - lineSize/2, x, borderSize/2 + lineSize/2);
            graphics.line(x, graphics.height - (borderSize/2 - lineSize/2), x, graphics.height - (borderSize/2 + lineSize/2));
          }

          for (int y = borderSize; y <= graphics.height - borderSize; y += borderSize) {
            int lineSize = borderSize/6;
            if (y % (borderSize * 4) == borderSize) {
              lineSize = borderSize/2;
            }
            graphics.line(borderSize/2 - lineSize/2, y, borderSize/2 + lineSize/2, y);
            graphics.line(graphics.width - (borderSize/2 - lineSize/2), y, graphics.width - (borderSize/2 + lineSize/2), y);
          }
        }
      });

      onFrameRenderers = setupRenderers(config.simSize, config.onFrame);
      onCompleteRenderers = setupRenderers(config.simSize, config.onComplete);
    } catch (IOException e) {
      e.printStackTrace();
      exit();
    }
  }

  private List<Renderer> setupRenderers(Size simSize, RendererConfig config) {
    List<Renderer> renderers = new ArrayList<>();

    if (config == null) {
      return renderers;
    }

    if (config.showPreview != null) {
      ShowPreview preview = config.showPreview;

      PreviewRenderer previewRenderer = new PreviewRenderer(this,
          simSize.width, simSize.height,
          width, height,
          (int) preview.backgroundColor,
          preview.framesPerRender);
      renderers.add(previewRenderer);
    }

    if (config.exportRaster != null) {
      ExportRaster export = config.exportRaster;
      RasterExportRenderer pngRenderer = new RasterExportRenderer(this,
          simSize.width, simSize.height,
          simSize.width * export.scale, simSize.height * export.scale,
          (int) export.backgroundColor, export.filename, export.framesPerRender);
      renderers.add(pngRenderer);
    }

    if (config.exportSvg != null) {
      ExportSvg export = config.exportSvg;
      SvgExportRenderer svgRenderer = new SvgExportRenderer(
          this, simSize.width, simSize.height, (int) export.backgroundColor, export.filename,
           export.framesPerRender);
      renderers.add(svgRenderer);
    }

    return renderers;
  }

  @Override
  public void draw() {
    if (!setup) {
      doSetup();
    }

    for (int i = 0; i < 10; i++) {
      stepSimulation();

      if (onFrameRenderers != null) {
        for (Renderer r : onFrameRenderers) {
          r.render(systems, localFrameCount);
        }
      }

      localFrameCount++;
    }

    if (localFrameCount >= config.maxFrames) {
      if (onCompleteRenderers != null) {
        for (Renderer r : onCompleteRenderers) {
          r.render(systems, localFrameCount);
        }
      }

      exit();
    }

    if (localFrameCount % 100 == 0) {
      System.out.println("Computed " + localFrameCount + "/" + config.maxFrames + " frames");
      System.out.println(particleSystem.getParticles().size() + " particles");
    }
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
    if (localFrameCount < threshold / 2) {
      for (Planet p : planetSystem.getPlanets()) {
        p.particle.setPosition(particleSystem.getBounds().getRandomPointInside(2));
      }
    }
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

  @Override
  public void keyPressed() {
    if (key == 'r' && config.renderPreview) {
      setupSimulation();
      localFrameCount = 0;
    }
  }
}
