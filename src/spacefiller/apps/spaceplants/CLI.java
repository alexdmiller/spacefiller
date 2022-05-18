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
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.spaceplants.System;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.planets.Planet;
import spacefiller.spaceplants.plants.PlantSystem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class CLI extends PApplet {
  private static final boolean RENDER_PREVIEW = true;

  private static String outputPath;
  private static Config config;
  private static Long seed;

  public static void main(String[] args) {
    outputPath = args[0];
    Rnd.init(args.length == 2 ? new Random(Long.valueOf(args[1])) : new Random());

    String configPath = "config.yml";

    try {
      ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      config = mapper.readValue(Paths.get(configPath).toFile(), Config.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    PApplet.main("spacefiller.apps.spaceplants.CLI");
  }

  private ParticleSystem particleSystem;
  private PGraphics canvas;
  private PGraphics finalRender;
  private PlantSystem plantSystem;
  private SymmetricRepel symmetricRepel;
  private DustSystem dustSystem;
  private BeeSystem beeSystem;

  private List<System> systems = new ArrayList<>();

  @Override
  public void settings() {
    size(config.simSize.width, config.simSize.height, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    Utils.init(this);
    noSmooth();

    canvas = createGraphics(config.simSize.width, config.simSize.height, P2D);
    canvas.noSmooth();
    canvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)canvas).textureSampling(3);

    finalRender = createGraphics(config.renderSize.width, config.renderSize.height, P2D);
    finalRender.noSmooth();
    finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)finalRender).textureSampling(3);

    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);

    frameRate(60);

    particleSystem = new ParticleSystem(canvas.width, canvas.height,15);

    if (config.dust != null) {
      dustSystem = new DustSystem(particleSystem, config.dust.count, canvas.width, canvas.height);
      systems.add(dustSystem);
    }

    RepelParticles repelDust = new RepelParticles(10, 0.1f);
    particleSystem.addBehavior(repelDust, ParticleTag.DUST);

    RepelParticles repelBees = new RepelParticles(10, 0.1f);
    particleSystem.addBehavior(repelBees, ParticleTag.BEE);

    if (config.circleConstraint != null) {
      particleSystem.addBehavior(new CircleBounds(config.circleConstraint.radius, 1, 0.1f));
    }

    particleSystem.addBehavior(new SoftBounds(10, 5, 3));

    if (config.plants != null) {
      plantSystem = new PlantSystem(particleSystem);
      systems.add(plantSystem);

      int numPlants = (int) Math.round(
          config.plants.min + Rnd.random.nextDouble() * (config.plants.max - config.plants.min));
      for (int i = 0; i < numPlants; i++) {
        plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2));
      }
    }

    if (config.hives != null) {
      beeSystem = new BeeSystem(particleSystem, plantSystem);
      systems.add(beeSystem);
      Params.set(PName.MAX_BEES_CREATED, config.hives.beesPerHive);

      int numHives = (int) Math.round(
          config.hives.min + Rnd.random.nextDouble() * (config.hives.max - config.hives.min));
      for (int i = 0; i < numHives; i++) {
        beeSystem.createHive(particleSystem.getBounds().getRandomPointInside(2));
      }
    }

    if (!RENDER_PREVIEW) {
      for (int i = 0; i < config.maxFrames; i++) {
        stepSimulation();
      }
      renderLarge();
    }
  }

  @Override
  public void draw() {
    if (RENDER_PREVIEW) {
      if (frameCount < config.maxFrames) {
        stepSimulation();
      }
      image(canvas, 0, 0, canvas.width, canvas.height);
    } else {
      exit();
    }
  }

  private void stepSimulation() {
    systems.forEach((system -> system.update()));
    particleSystem.update();

    canvas.beginDraw();
    canvas.background(0);

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

    finalRender.save(outputPath);
  }
}
