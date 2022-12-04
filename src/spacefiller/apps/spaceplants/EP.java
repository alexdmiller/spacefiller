package spacefiller.apps.spaceplants;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.event.MouseEvent;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PJOGL;
import spacefiller.Utils;
import spacefiller.spaceplants.bees.BeeSystem;
import spacefiller.spaceplants.dust.DustSystem;
import spacefiller.math.sdf.FloatField2;
import spacefiller.math.Metaballs;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleSystem;
import spacefiller.particles.ParticleTag;
import spacefiller.particles.behaviors.*;
import spacefiller.spaceplants.planets.Planet;
import spacefiller.spaceplants.plants.PlantSystem;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static spacefiller.math.sdf.FloatField2.debugDraw;

public class EP extends PApplet {
  public static final int SIMULATION_WIDTH = 18 * 10;
  public static final int SIMULATION_HEIGHT = 24 * 10;

  public static final int RENDER_SCALE = 5;
  public static final int PREVIEW_SCALE = 4;

  enum Tool {
    SEED, HIVE, MARBLE
  }

  public static void main(String[] args) {
    main("spacefiller.apps.spaceplants.EP");
  }

  private List<Metaballs> metaballs;

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

  @Override
  public void settings() {
    size(SIMULATION_WIDTH * PREVIEW_SCALE, SIMULATION_HEIGHT * PREVIEW_SCALE, P2D);
    PJOGL.profile = 1;
  }

  Function<ParticleTag, FloatField2> sdf = (tag) -> (x, y) -> {
    Vector position = new Vector(x, y);

    // Metaballs style SDF
    double sum = 1 - planets.stream().mapToDouble(p -> {
      Planet.Range range = p.getRange(tag);

      if (range == null) {
        return 0;
      }

      Vector dist = Vector.sub(position, p.getPosition());
      return Math.pow((range.getOuter() / 2 - range.getInner() / 2), 2) /
          Math.pow(
              dist.magnitude() - (range.getOuter() / 2 + range.getInner() / 2),
              2);
    }).sum();

    // sum += (Utils.noise(x * noiseScale, y * noiseScale, frameCount/100f) - 0.5f) * noiseAmount;

    if (sum < 0) {
      sum = 0;
    }

    return (float) sum;
  };

  Map<ParticleTag, Float> steepness = new HashMap<ParticleTag, Float>() {
    {
      put(ParticleTag.BEE, 0.1f);
      put(ParticleTag.HIVE, 0.7f);
      put(ParticleTag.PLANT, 0.5f);
      put(ParticleTag.MARBLE, 1f);
      put(ParticleTag.DUST, 0.1f);
    }
  };

  @Override
  public void setup() {
    Utils.init(this);
    noSmooth();

    canvas = createGraphics(SIMULATION_WIDTH, SIMULATION_HEIGHT, P2D);
    canvas.noSmooth();
    canvas.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)canvas).textureSampling(3);

    finalRender = createGraphics(SIMULATION_WIDTH * RENDER_SCALE, SIMULATION_HEIGHT * RENDER_SCALE, P2D);
    finalRender.noSmooth();
    finalRender.hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)finalRender).textureSampling(3);

    hint(DISABLE_TEXTURE_MIPMAPS);
    ((PGraphicsOpenGL)g).textureSampling(3);


    frameRate(60);

    float atmosphere = 50;

    planets = new ArrayList<>();
    Planet planet1 = new Planet(new Vector(canvas.width / 2 - 200, canvas.height / 2 + 200));
    planet1.addRange(0, 20, ParticleTag.MARBLE);
    planet1.addRange(20, 70, ParticleTag.HIVE);
    planet1.addRange(70, 150, ParticleTag.PLANT);
    planet1.addRange(150, 180, ParticleTag.BEE);
    planet1.addRange(150, 150 + atmosphere, ParticleTag.DUST);
    planets.add(planet1);

//    Planet planet2 = new Planet(new Vector(canvas.width / 2, canvas.height / 2));
//    planet2.addRange(0, 50, ParticleTag.HIVE);
//    planets.add(planet2);

    Planet planet3 = new Planet(new Vector(canvas.width / 2 + 200, canvas.height / 2 - 200));
    planet3.addRange(0, 90, ParticleTag.PLANT);
    planet3.addRange(90, 90 + 30, ParticleTag.BEE);
    planet3.addRange(90, 90 + atmosphere, ParticleTag.DUST);
    planets.add(planet3);

    Planet planet4 = new Planet(new Vector(canvas.width / 2 + 200, canvas.height / 2 + 200));
    planet4.addRange(0, 40, ParticleTag.HIVE);
    planet4.addRange(0, 40 + 30, ParticleTag.BEE);
    planet4.addRange(40, 40 + atmosphere, ParticleTag.DUST);
    planets.add(planet4);

    Planet planet5 = new Planet(new Vector(canvas.width / 2 - 200, canvas.height / 2 - 200));
    planet5.addRange(0, 50, ParticleTag.PLANT);
    planet5.addRange(90, 50 + 20, ParticleTag.BEE);
    planet5.addRange(90, 90 + atmosphere, ParticleTag.DUST);
    planets.add(planet5);

    Planet planet6 = new Planet(new Vector(canvas.width / 2, canvas.height / 2));
    planet6.addRange(70, 90, ParticleTag.PLANT);
    planet6.addRange(50, 90, ParticleTag.BEE);
    planets.add(planet6);

    particleSystem = new ParticleSystem(canvas.width, canvas.height,15);
    dustSystem = new DustSystem(particleSystem, canvas.width, canvas.height);
    beeSystem = new BeeSystem(particleSystem, plantSystem);

    gradients = Arrays.stream(ParticleTag.values())
        // Filter down to tags that are actively used in planets
        .filter(tag -> planets.stream().anyMatch(p -> p.getRange(tag) != null))

        // Create a gradient behavior for each tag
        .map(tag -> {
          FollowGradient followGradient = new FollowGradient(
              sdf.apply(tag),
              steepness.getOrDefault(tag, 1f),
              true);
          followGradient.setTagConstraint(tag);
          return followGradient;
        })
        .collect(Collectors.toList());

    gradients.forEach(gradient -> particleSystem.addBehavior(gradient));

//    particleSystem.addBehavior(new FollowGradient(core, 100), ParticleTag.MARBLE);
//    particleSystem.addBehavior(new FollowGradient(innerLayer, 100), ParticleTag.HIVE);
//    particleSystem.addBehavior(new FollowGradient(midLayer, 100), ParticleTag.PLANT);
//    particleSystem.addBehavior(new FollowGradient(outerLayer, 50), ParticleTag.BEE);
//    particleSystem.addBehavior(new FollowGradient(stratosphere, 10), ParticleTag.DUST);

    symmetricRepel = new SymmetricRepel(5, 1);
    particleSystem.addBehavior(symmetricRepel, ParticleTag.GLOBAL_REPEL);

    RepelParticles repelDust = new RepelParticles(15, 0.5f);
    particleSystem.addBehavior(repelDust, ParticleTag.DUST);

//    particleSystem.addBehavior(new ParticleFriction(0.9f));
//    particleSystem.addBehavior(new RepelParticles(20, 5));

    particleSystem.addBehavior(new SoftBounds());
    plantSystem = new PlantSystem(particleSystem);
  }

  @Override
  public void draw() {
    background(0);

    if (selected != null) {
      selected.getPosition().set(mouseX / PREVIEW_SCALE, mouseY / PREVIEW_SCALE);
    }

    for (int i = 0; i < 1; i++) {
      plantSystem.update();
      dustSystem.update();
      beeSystem.update();
      particleSystem.update();
    }

    canvas.beginDraw();
    canvas.background(0);

    if (debugIndex >= 0) {
      FloatField2 field = gradients.get(debugIndex).getField();
      debugDraw(field, 5, canvas);
      canvas.fill(0);
      canvas.text(gradients.get(debugIndex).getTag().toString(), 15, 15);
    }

    if (drawOutlines) {
      planets.forEach(planet -> planet.drawDebug(canvas));
      canvas.fill(255);
      canvas.text(Tool.values()[selectedTag].toString(), 50, 15);
    }

    canvas.noStroke();
    canvas.fill(255);

    beeSystem.setLightLevel(1);
    plantSystem.setLightLevel(1);

    dustSystem.draw(canvas);
    plantSystem.draw(canvas);
    beeSystem.draw(canvas);

    Planet planet = planets.get(planets.size() - 1);
    canvas.stroke(255, 255);
    canvas.strokeWeight(2);

    canvas.endDraw();

    finalRender.beginDraw();
    finalRender.clear();

    if (saveFrame) {
      render(0x0, 0x0, 0x0, 0, 50);
      saveFrame = false;
    } else {
      // finalRender.blendMode(PConstants.ADD);
      finalRender.image(canvas,
          0, 0,
          canvas.width * RENDER_SCALE,
          canvas.height * RENDER_SCALE);
      finalRender.blendMode(PConstants.BLEND);
    }
    finalRender.endDraw();

    image(finalRender,
        0, 0,
        finalRender.width / RENDER_SCALE * PREVIEW_SCALE,
        finalRender.height / RENDER_SCALE * PREVIEW_SCALE);
  }

  private void render(int innerColor, int outerColor1, int outerColor2, float padding, int resolution) {
    finalRender.clear();

    // finalRender.blendMode(PConstants.ADD);
    finalRender.image(canvas,
        0, 0,
        canvas.width * RENDER_SCALE,
        canvas.height * RENDER_SCALE);
    finalRender.blendMode(PConstants.BLEND);

    finalRender.save((int) (Math.random() * 10000) + ".png");
  }

  @Override public void mousePressed(MouseEvent event) {
    Vector mouse = new Vector(mouseX / PREVIEW_SCALE, mouseY / PREVIEW_SCALE);

    if (event.isShiftDown()) {
      switch (Tool.values()[selectedTag]) {
        case HIVE:
          // beeSystem.createHive(mouse, Params);
          break;
        case SEED:
          plantSystem.createSeed(mouse);
          break;
      }
    } else {
      selected = planets.stream().filter(planet -> planet.getPosition().dist(mouse) < 100).findFirst().orElse(null);
    }
  }

  @Override public void mouseReleased() {
    selected = null;
  }

  @Override public void keyPressed() {
    switch (key) {
      case 'd' :
        debugIndex++;
        if (debugIndex >= gradients.size()) {
          debugIndex = -1;
        }
        break;
      case 'o':
        drawOutlines = !drawOutlines;
      case 't' :
        selectedTag = (selectedTag + 1) % Tool.values().length;
        break;
      case 's':
        saveFrame = true;
        break;
      case 'c':
        for (int i = 0; i < particleSystem.getParticles().size(); i++) {
          particleSystem.getParticles().get(i).setUserData("kill", true);
        }
        break;
      case 'r':
        for (int i = 0; i < 20; i++) {
          plantSystem.createSeed(new Vector(
              random(canvas.width),
              random(canvas.height)));
        }

        for (int i = 0; i < 5; i++) {
//          beeSystem.createHive(new Vector(
//              random(canvas.width),
//              random(canvas.width)));
        }


        break;
    }
  }
}
