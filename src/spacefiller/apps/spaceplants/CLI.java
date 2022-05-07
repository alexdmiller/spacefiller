package spacefiller.apps.spaceplants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.Utils;
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
  private static String outputPath;
  private static Config config;

  public static void main(String[] args) {
    outputPath = args[0];

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

  private Planet selected;
  private int debugIndex = -1;
  private boolean drawOutlines = true;

  private float noiseScale = 0.01f;
  private float noiseAmount = 1;

  private List<Planet> planets;
  private List<FollowGradient> gradients;
  private int selectedTag;
  private boolean saveFrame;

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

    beeSystem = new BeeSystem(particleSystem, plantSystem);
    systems.add(beeSystem);

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
          config.plants.min + Math.random() * (config.plants.max - config.plants.min));
      for (int i = 0; i < numPlants; i++) {
        plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2));
      }
    }

    for (int i = 0; i < config.maxFrames; i++) {
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

    renderLarge();
  }

  @Override
  public void draw() {
    exit();
  }

  private void renderLarge() {
    java.lang.System.out.println("Rendering image to " + outputPath);

    finalRender.clear();

    // finalRender.blendMode(PConstants.ADD);
    finalRender.image(canvas,
        0, 0,
        config.renderSize.width,
        config.renderSize.height);
    finalRender.blendMode(PConstants.BLEND);

    finalRender.save(outputPath);
  }
}
