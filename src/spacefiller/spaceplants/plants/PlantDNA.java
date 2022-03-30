package spacefiller.spaceplants.plants;

import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;

public class PlantDNA {
  public static PlantDNA createNewDNA() {
    float r = (float) Math.random();
    if (r < 0.25) {
      if (Math.random() < 0.5f) {
        return new PlantDNA()
            .setBranchChance(0.1f)
            .setAliveBranchColor(0xff77BF00)
            .setAliveFlowerColor(0xff52FF00)
            .setBranchingFactor(2)
            .setMaxDepth(20);
      } else {
        return new PlantDNA()
            .setAliveBranchColor(0xff43BF00)
            .setBranchChance(0.4f);
      }
    } else if (r < 0.5) {
      int[] summerColors = new int[] {
        0xffFF3737, 0xffF5FF37, 0xff37FFC9, 0xffB75CFF
      };

      int[] summerBranchColors = new int[] {
          0xff2BA637, 0xff23872D, 0xff318723
      };

      float flowerSize = (float) (3 + Math.random() * 4);

      // small fractal standard
      return new PlantDNA()
          .setAliveFlowerColor(summerColors[(int) (Math.random() * summerColors.length)])
          .setAliveBranchColor(summerBranchColors[(int) (Math.random() * summerBranchColors.length)])
          .setFlowerSize(flowerSize)
          .setBudMaxSize(flowerSize);
    } else if (r < 0.75) {
        return new PlantDNA()
            .setMaxDepth(10)
            .setBranchingFactor(4)
            .setSeedBranchingFactor(4)
            .setBranchingFactorFalloff(-1f)
            .setBranchChance(0.5f);
    } else {
      // long blue dudes
      if (Math.random() < 0.2) {
        return new PlantDNA()
            .setMaxDepth(40)
            .setMaxDepthDeviation(10)
            .setMass(0.5f)
            .setAliveBranchColor(0xff0052CC)
            .setAliveFlowerColor(0xff24C4FF)
            .setSeedBranchingFactor(5)
            .setBranchChance(0)
            .setBudMaxSize(6)
            .setFlowerSize(10);
      } else {
        int[] winterFlowerColor = new int[] {
            0xff5FC786, 0xffC72E9C
        };
        return new PlantDNA()
            .setMaxDepth(3)
            .setMaxDepthDeviation(2)
            .setAliveBranchColor(0xff5FC786)
            .setAliveFlowerColor(winterFlowerColor[(int) (Math.random() * winterFlowerColor.length)])
            .setSeedBranchingFactor(20)
            .setBranchingFactorDeviation(10)
            .setBranchChance(0)
            .setBudMaxSize(6)
            .setFlowerSize(6);
      }
    }
  }

  private float plantEntropy = (float) Math.random();
  private float particleMass = 1;
  private int aliveBranchColor = 0xff53982D;
  private int dyingBranchColor = 0xff333333;
  private int aliveBranchColorNight = 0xff303C27;
  private int dyingBranchColorNight = 0xff333333;
  private int aliveFlowerColor = 0xfff5198b;
  private int deadColorFlower = 0xff000000;
  private int aliveFlowerColorNight = 0xff130F12;
  private int deadColorFlowerNight = 0xff000000;
  private int excitedFlowerColor = 0xffffffff;
  private int budColor = 0xff1D7E00;
  private int budColorNight = 0xff071C00;
  private int seedColor = 0xff93ca5d;
  private int maxAge = (int) (Params.i(PName.MAX_PLANT_AGE)
      - Math.round(Math.random() * Params.i(PName.MAX_PLANT_AGE) * Params.f(PName.PLANT_AGE_RANDOMNESS)));
  private int timeToPlant = Params.i(PName.TIME_TO_PLANT);
  private float connectionLength = 10;
  private int branchingFactor = 7;
  private float branchingFactorFalloff = 0;
  private int seedBranchingFactor = 4;
  private int branchingFactorDeviation = 0;
  private int maxDepth = 4;
  private int maxDepthDeviation = 0;
  private float excitementGrowth = 1;
  private float branchChance = 0.5f;
  private float budMaxSize = 3;
  private float flowerSize = 4;
  private float seedSize = 3;

  public PlantDNA setMass(float value) {
    this.particleMass = value;
    return this;
  }

  public PlantDNA setBranchingFactorDeviation(int value) {
    this.branchingFactorDeviation = value;
    return this;
  }

  public PlantDNA setPlantEntropy(float plantEntropy) {
    this.plantEntropy = plantEntropy;
    return this;
  }

  public PlantDNA setAliveBranchColor(int aliveBranchColor) {
    this.aliveBranchColor = aliveBranchColor;
    return this;
  }

  public PlantDNA setDyingBranchColor(int dyingBranchColor) {
    this.dyingBranchColor = dyingBranchColor;
    return this;
  }

  public PlantDNA setAliveBranchColorNight(int aliveBranchColorNight) {
    this.aliveBranchColorNight = aliveBranchColorNight;
    return this;
  }

  public PlantDNA setDyingBranchColorNight(int dyingBranchColorNight) {
    this.dyingBranchColorNight = dyingBranchColorNight;
    return this;
  }

  public PlantDNA setAliveFlowerColor(int aliveFlowerColor) {
    this.aliveFlowerColor = aliveFlowerColor;
    return this;
  }

  public PlantDNA setDeadColorFlower(int deadColorFlower) {
    this.deadColorFlower = deadColorFlower;
    return this;
  }

  public PlantDNA setAliveFlowerColorNight(int aliveFlowerColorNight) {
    this.aliveFlowerColorNight = aliveFlowerColorNight;
    return this;
  }

  public PlantDNA setDeadColorFlowerNight(int deadColorFlowerNight) {
    this.deadColorFlowerNight = deadColorFlowerNight;
    return this;
  }

  public PlantDNA setExcitedFlowerColor(int excitedFlowerColor) {
    this.excitedFlowerColor = excitedFlowerColor;
    return this;
  }

  public PlantDNA setBudColor(int budColor) {
    this.budColor = budColor;
    return this;
  }

  public PlantDNA setBudColorNight(int budColorNight) {
    this.budColorNight = budColorNight;
    return this;
  }

  public PlantDNA setSeedColor(int seedColor) {
    this.seedColor = seedColor;
    return this;
  }

  public PlantDNA setMaxAge(int maxAge) {
    this.maxAge = maxAge;
    return this;
  }

  public PlantDNA setTimeToPlant(int timeToPlant) {
    this.timeToPlant = timeToPlant;
    return this;
  }

  public PlantDNA setConnectionLength(float connectionLength) {
    this.connectionLength = connectionLength;
    return this;
  }

  public PlantDNA setBranchingFactor(int branchingFactor) {
    this.branchingFactor = branchingFactor;
    return this;
  }

  public PlantDNA setSeedBranchingFactor(int seedBranchingFactor) {
    this.seedBranchingFactor = seedBranchingFactor;
    return this;
  }

  public PlantDNA setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
    return this;
  }

  public PlantDNA setExcitementGrowth(float excitementGrowth) {
    this.excitementGrowth = excitementGrowth;
    return this;
  }

  public PlantDNA setBranchChance(float branchChance) {
    this.branchChance = branchChance;
    return this;
  }

  public PlantDNA setBudMaxSize(float budMaxSize) {
    this.budMaxSize = budMaxSize;
    return this;
  }

  public PlantDNA setFlowerSize(float flowerSize) {
    this.flowerSize = flowerSize;
    return this;
  }

  public PlantDNA setSeedSize(float seedSize) {
    this.seedSize = seedSize;
    return this;
  }

  public float getParticleMass() {
    return particleMass;
  }

  public int getAliveBranchColor() {
    return aliveBranchColor;
  }

  public int getDyingBranchColor() {
    return dyingBranchColor;
  }

  public int getAliveFlowerColor() {
    return aliveFlowerColor;
  }

  public int getDeadColorFlower() {
    return deadColorFlower;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public float getConnectionLength() {
    return connectionLength;
  }

  public int getBranchingFactor() {
    return branchingFactor;
  }

  public int getMaxDepth() {
    return maxDepth;
  }

  public float getBudMaxSize() {
    return budMaxSize;
  }

  public float getFlowerSize() {
    return flowerSize;
  }

  public float getExcitementGrowth() {
    // get your mind out of the gutter
    return excitementGrowth;
  }

  public float getBranchChance() {
    return branchChance;
  }

  public int getSeedBranchingFactor() {
    return seedBranchingFactor;
  }

  public int getTimeToPlant() {
    return timeToPlant;
  }

  public int getSeedColor() {
    return seedColor;
  }

  public float getSeedSize() {
    return seedSize;
  }

  public int getAliveBranchColorNight() {
    return aliveBranchColorNight;
  }

  public int getDyingBranchColorNight() {
    return dyingBranchColorNight;
  }

  public float getPlantEntropy() {
    return plantEntropy;
  }

  public int getAliveFlowerColorNight() {
    return aliveFlowerColorNight;
  }

  public int getDeadColorFlowerNight() {
    return deadColorFlowerNight;
  }

  public int getExcitedFlowerColor() {
    return excitedFlowerColor;
  }

  public int getBudColor() {
    return budColor;
  }

  public int getBudColorNight() {
    return budColorNight;
  }

  public PlantDNA setMaxDepthDeviation(int maxDepthDeviation) {
    this.maxDepthDeviation = maxDepthDeviation;
    return this;
  }

  public int getMaxDepthDeviation() {
    return maxDepthDeviation;
  }

  public int getBranchingFactorDeviation() {
    return branchingFactorDeviation;
  }

  public PlantDNA setBranchingFactorFalloff(float branchingFactorFalloff) {
    this.branchingFactorFalloff = branchingFactorFalloff;
    return this;
  }

  public float getBranchingFactorFalloff() {
    return branchingFactorFalloff;
  }
}
