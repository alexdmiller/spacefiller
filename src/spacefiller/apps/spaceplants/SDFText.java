package spacefiller.apps.spaceplants;

import processing.core.*;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.Utils;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.math.FloatField2;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.FollowGradient;
import spacefiller.particles.behaviors.RepelParticles;
import spacefiller.particles.behaviors.SoftBounds;
import spacefiller.particles.behaviors.SymmetricRepel;
import spacefiller.spaceplants.planets.Planet;
import spacefiller.spaceplants.plants.PlantDNA;
import spacefiller.spaceplants.plants.PlantSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spacefiller.math.FloatField2.debugDraw;
import static spacefiller.math.FloatField2.gradient;

public class SDFText extends PApplet {
  public static void main(String[] args) {
    main("spacefiller.apps.spaceplants.SDFText");
  }

  // poster dimensions
  public static final int SIMULATION_WIDTH = 24 * 16;
  public static final int SIMULATION_HEIGHT = 24 * 16;
  public static final int FONT_SIZE = 230;

  public static final int RENDER_SCALE = 8;
  public static final int PREVIEW_SCALE = 1;

  private int iterations = 0;
  private static final int MAX_ITERATIONS = 700;

  private PGraphics canvas;
  private int[] map;
  private ParticleSystem particleSystem;
  private PGraphics finalRender;
  private PlantSystem plantSystem;
  private SymmetricRepel symmetricRepel;
  private DustSystem dustSystem;
  private BeeSystem beeSystem;
  private PFont font;
  private boolean saveFrame;

  FloatField2 field = (x, y) -> {
    if (x < 0 || y < 0 || x >= canvas.width || y >= canvas.height) {
      return 0;
    }
    return MAX_ITERATIONS - map[((int) y) * canvas.width + (int) x];
  };

  FloatField2 field2 = (x, y) -> {
    if (x < 0 || y < 0 || x >= canvas.width || y >= canvas.height) {
      return 0;
    }
    return MAX_ITERATIONS - min(map[((int) y) * canvas.width + (int) x], (MAX_ITERATIONS - 50));
  };

  @Override
  public void settings() {
    size(SIMULATION_WIDTH * PREVIEW_SCALE, SIMULATION_HEIGHT * PREVIEW_SCALE, P2D);
    PJOGL.profile = 1;
  }

  @Override
  public void setup() {
    Utils.init(this);

    frameRate(60);

    canvas = createGraphics(SIMULATION_WIDTH, SIMULATION_HEIGHT, P2D);
    canvas.noSmooth();
    canvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)canvas).textureSampling(3);

    finalRender = createGraphics(SIMULATION_WIDTH * RENDER_SCALE, SIMULATION_HEIGHT * RENDER_SCALE, P2D);
    finalRender.noSmooth();
    finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)finalRender).textureSampling(3);

    canvas.beginDraw();
//    font = createFont("/System/Library/Fonts/Supplemental/Arial Rounded Bold.ttf", FONT_SIZE/2);
//    canvas.textFont(font);
//    canvas.background(0);
//    canvas.textAlign(CENTER);
//    canvas.textSize(FONT_SIZE);
//    canvas.fill(255);
//    canvas.text("ROBYN", canvas.width / 2, canvas.height / 2 - 80);
//    canvas.endDraw();
//
//    canvas.beginDraw();
//    canvas.text("MISCHA", canvas.width / 2, canvas.height / 2 + 230);

//    PImage logoImage = loadImage("logo.png");
//    int logoWidth = (int) (logoImage.width * 0.5f);
//    int logoHeight = (int) (logoImage.height * 0.5f);
//    canvas.image(logoImage,
//        canvas.width / 2 - logoWidth / 2,
//        canvas.height / 2 - logoHeight / 2,
//        logoWidth,
//        logoHeight);

    canvas.ellipse(canvas.width / 2,canvas.height / 2, canvas.width - 100, canvas.height - 100);
    canvas.endDraw();

    PImage image = canvas.get();
    map = new int[image.width * image.height];
    for (int x = 1; x < image.width - 1; x++) {
      for (int y = 1; y < image.height - 1; y++) {
        map[y * image.width + x] = image.get(x, y) == color(255) ? MAX_ITERATIONS : 0;
      }
    }

    image(image, 0, 0);

    particleSystem = new ParticleSystem(canvas.width, canvas.height,15);
    dustSystem = new DustSystem(particleSystem, 250, canvas.width, canvas.height);
    beeSystem = new BeeSystem(particleSystem, plantSystem);

    RepelParticles repelDust = new RepelParticles(10, 0.1f);
    particleSystem.addBehavior(repelDust, ParticleTag.DUST);

    RepelParticles repelBees = new RepelParticles(10, 0.1f);
    particleSystem.addBehavior(repelBees, ParticleTag.BEE);

    particleSystem.addBehavior(new SoftBounds(10, 5, 3));
    plantSystem = new PlantSystem(particleSystem);

    particleSystem.addBehavior(new FollowGradient(field, 1f, true), ParticleTag.HIVE);
    particleSystem.addBehavior(new FollowGradient(field, 1f, true), ParticleTag.PLANT);
    particleSystem.addBehavior(new FollowGradient(field, 1f, true), ParticleTag.SEED);
    particleSystem.addBehavior(new FollowGradient(field2, 1f, true), ParticleTag.BEE);
    particleSystem.addBehavior(new FollowGradient(field2, 1f, true), ParticleTag.DUST);

    int numPlants = 4;
    for (int i = 0; i < numPlants; i++) {
      plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2));
    }

    int numHives = 2;
    for (int i = 0; i < numHives; i++) {
//      beeSystem.createHive(particleSystem.getBounds().getRandomPointInside(2));
    }

    plantSystem.createSeed(particleSystem.getBounds().getRandomPointInside(2), new PlantDNA().setAliveBranchColor(0xffffffff)
        .setBranchingFactor(1)
        .setBranchChance(0.1f)
        .setFlowerSize(4)
        .setMaxDepth(50));
  }

  @Override
  public void draw() {
    canvas.beginDraw();
    canvas.background(0);

    if (iterations < MAX_ITERATIONS) {
      int steps = 10;
      for (int i = 0; i < steps; i++) {
        for (int x = 1; x < canvas.width - 1; x++) {
          for (int y = 1; y < canvas.height - 1; y++) {
            int center = map[x + (y) * canvas.width];
            int top = map[x + (y - 1) * canvas.width];
            int bottom = map[x + (y + 1) * canvas.width];
            int left = map[(x - 1) + y * canvas.width];
            int right = map[(x + 1) + y * canvas.width];
            int max = max(new int[]{top, bottom, left, right});

            if (max > center) {
              map[x + y * canvas.width] = max - 2;
            }
          }
        }
      }
      iterations += steps;

      canvas.loadPixels();
      for (int x = 1; x < canvas.width - 1; x++) {
        for (int y = 1; y < canvas.height - 1; y++) {
          canvas.set(x, y, color((int) (map[y * canvas.width + x] / (float) MAX_ITERATIONS * 255)));
        }
      }
      canvas.updatePixels();

    } else {

      for (int i = 0; i < 10; i++) {
        plantSystem.update();
        dustSystem.update();
        beeSystem.update();
        particleSystem.update();
      }


      beeSystem.setLightLevel(1);
      plantSystem.setLightLevel(1);

      dustSystem.draw(canvas);
      plantSystem.draw(canvas);
      beeSystem.draw(canvas);
    }


//    Vector mouse = new Vector((float) mouseX / width * canvas.width, (float) mouseY / height * canvas.height);
//    Vector gradient = gradient(field, mouse);
//    canvas.stroke(255);
//    canvas.strokeWeight(2);
//    canvas.line(mouse.x, mouse.y, mouse.x + gradient.x * 100, mouse.y + gradient.y * 100);


    canvas.endDraw();

    image(canvas, 0, 0, width, height);

    if (saveFrame) {
      renderLarge();
      saveFrame = false;
    }
  }

  private void renderLarge() {
    Path path = Paths.get("renders/large");

    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
    finalRender.beginDraw();
    finalRender.clear();

    // finalRender.blendMode(PConstants.ADD);
    finalRender.image(canvas,
        0, 0,
        canvas.width * RENDER_SCALE,
        canvas.height * RENDER_SCALE);
    finalRender.blendMode(PConstants.BLEND);

    finalRender.save("renders/large/poster.png");
    finalRender.endDraw();
  }

  public void keyPressed() {
    saveFrame = true;
  }

  public void mousePressed() {
    Vector mouse = new Vector((float) mouseX / width * canvas.width, (float) mouseY / height * canvas.height);
    plantSystem.createSeed(mouse);
  }
}
