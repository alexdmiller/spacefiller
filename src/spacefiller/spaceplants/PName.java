package spacefiller.spaceplants;

import static spacefiller.spaceplants.Params.TEST_MODE;

public enum PName {

  /*
  In the comments below, ppf stands for "probability-per-frame". This
  refers to the following common pattern in this code:

  if (Math.random() < THRESHOLD) { ... }

  The system is updated a maximum of 60 fps.

  60 fps = 5184000 frames/day

  Here's a guide to how often an event will happen for various ppf values:

  ppf      expected frequency
  0.000001 5 times/day
  0.00001  2 times/hour
  0.0001   22 times/hour
  0.001    3 times/minute
  0.01     every other second

  Note that sometimes these ppf gates are used within tree structures (like
  plants) that can expand exponentially.
  */
  SIMULATION_UPDATES_PER_FRAME(5),

  MIN_CAMERA_BRIGHTNESS(0f),
  MAX_CAMERA_BRIGHTNESS(1.0f),

  // ppf of a plant node dying after it has been condemned.
  // higher values result in quicker plant deaths after seed
  // dies.
  CONDEMNED_DEATH_CHANCE(ppf(0.001f)),

  // number of frames it takes for a bud to turn into a flower.
  // flowers can be plucked by worms and turned into plants.
  BUD_TO_FLOWER_TIME(hours(0.001f)),

  PLANT_AGE_RANDOMNESS(0.3f),

  // ppf a plant node will grow a connected node
  GROWTH_PROBABILITY(ppf(0.1f)),

  // fluid velocity threshold it takes to excite an entity.
  // turn down for less crazy interaction. turn up for
  // more responsive interaction.
  EXCITEMENT_THRESHOLD(0.6f),

  // how quickly particles get unexcited
  EXCITEMENT_FALLOFF(0.9f),

  SEED_DESIRED_HIVE_DISTANCE(50f),
  SEED_DESIRED_SEED_DISTANCE(10f),

  NIGHT_THRESHOLD(0.2f),

  NIGHT_BACKGROUND_COLOR(0xff00010B),
  // DAY_BACKGROUND_COLOR(0xff374865),
  // DAY_BACKGROUND_COLOR(0xff253F6C),
  // DAY_BACKGROUND_COLOR(0xff4C6DA6),
  // DAY_BACKGROUND_COLOR(0xff13213C),
  DAY_BACKGROUND_COLOR(0xff304979),


  FLYTRAP_CORE_REPULSION(300f),
  FLYTRAP_SCARE_TIMER(hours(3)),
  //FLYTRAP_STRIKE_CHANCE(0.4f),
  //FLYTRAP_STRIKE_SUCSESS(0.3f),
  FLYTRAP_CATCH_BEE_MAX_CHANCE(ppf(0.7f)),

  // how slowly plant lights oscillate
  PLANT_LIGHT_PERIOD(500),

  // larger number -> lights are "stretched out"
  PLANT_LIGHT_SPACING(2f),

  // chance that a worm will grab a flower if it sees one
  GRAB_FLOWER_CHANCE(ppf(0.0001f)),

  FOOD_TO_WORM_CHANCE(ppf(0.00001f)),

  SEED_LIFESPAN(hours(5)),

  FLOWER_DETACHED_LIFESPAN(minutes(0)),
  TIME_TO_PLANT(hours(0.1f)),

  MAX_PLANT_PARTICLES(5000),
  MIN_PLANT_PARTICLES(1000),

  MAX_BEES(1000),
  MIN_BEES(100),

  MAX_HIVE_FOOD(8),

  BEE_GROWTH_TIMER(hours(0.000000001f)),

  FLYTRAP_STRIKE_CHANCE(ppf(0.1f)),
  HIVE_DESIRED_FLYTRAP_DISTANCE(200f),

  FLOWER_TO_SEED_CHANCE(0f),

  MAX_PLANT_DENSITY(0.1f),

  STARTING_BABIES_PER_HIVE(20),
  MAX_BEES_CREATED(20),

  FLYTRAP_REGROUP_TIME(hours(10)),

  STROKE_WEIGHT(1f),

  FLUID_VELOCITY_SCALE(0.03f),

  DANGEROUS_FLUID_VELOCITY(0.2f),

  MAX_PLANT_AGE(days(100f)),
  HIVE_LIFETIME(days(100f)),
  WORM_LIFETIME(days(100f)),
  FLYTRAP_LIFESPAN(days(0.2f)),
  TRASH_LIFESPAN(days(0.3f)),

  MAXIMUM_PARTICLE_LIFESPAN(1000000000),

  MAX_HIVE_SIZE(20);

  private static final float TIMER_MULTIPLIER = 0.01f;
  private static final float PROBABILITY_MULTIPLIER = TEST_MODE ? 100f : 1;

  private static int seconds(float seconds) {
    return Math.round(60 * seconds * TIMER_MULTIPLIER);
  }

  private static int minutes(float mins) {
    return Math.round(60 * 60 * mins * TIMER_MULTIPLIER);
  }

  private static int hours(float hours) {
    return Math.round(60 * 60 * 60 * hours * TIMER_MULTIPLIER);
  }

  private static int days(float days) {
    return Math.round(60 * 60 * 60 * 24 * days * TIMER_MULTIPLIER);
  }

  private static float ppf(float probability) {
    return probability * PROBABILITY_MULTIPLIER;
  }

  private Object defaultValue;

  PName(Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }
}