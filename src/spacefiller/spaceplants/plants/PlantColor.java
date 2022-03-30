package spacefiller.spaceplants.plants;

import processing.core.PApplet;
import spacefiller.spaceplants.PName;
import spacefiller.spaceplants.Params;
import spacefiller.Utils;

import static processing.core.PConstants.RGB;

public class PlantColor {
  private float lightLevel;

  public PlantColor() {
    this.lightLevel = 0;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }

  public int getBranchColor(PlantDNA dna, int age) {

    float t = (float) age / dna.getMaxAge();
    t = t < 0.95f ? 0f : PApplet.map(t, 0.95f, 1f, 0, 1);

    int dayColor = PApplet.lerpColor(dna.getAliveBranchColor(), dna.getDyingBranchColor(), t, RGB);
    int nightColor = PApplet.lerpColor(dna.getAliveBranchColorNight(), dna.getDyingBranchColorNight(), t, RGB);
    return PApplet.lerpColor(nightColor, dayColor, lightLevel, RGB);
  }

  public int getFlowerColor(PlantDNA dna, int age, float excitement) {
    float t = (float) age / dna.getMaxAge();

    int dayColor = dna.getAliveFlowerColor(); //Utils.lerpColor(dna.getAliveFlowerColor(), dna.getDeadColorFlower(), t);
    int nightColor = dna.getAliveFlowerColorNight(); //Utils.lerpColor(dna.getAliveFlowerColorNight(), dna.getDeadColorFlowerNight(), t);
    int normalColor = PApplet.lerpColor(nightColor, dayColor, lightLevel, RGB);

    return PApplet.lerpColor(normalColor, dna.getExcitedFlowerColor(), excitement, RGB);
  }

  public int getFlowerLightColor(PlantDNA dna, int age) {
    float t = (float) age / dna.getMaxAge();

    // int dayColor = Utils.lerpColor(dna.getAliveFlowerColor(), dna.getDeadColorFlower(), t);
    return dna.getAliveFlowerColor();
  }

  public int getFlowerBudColor(PlantDNA dna, int life) {
    float t = (float) life / Params.i(PName.BUD_TO_FLOWER_TIME);
    int dayColor = PApplet.lerpColor(dna.getAliveBranchColor(), dna.getBudColor(), t, RGB);
    int nightColor = PApplet.lerpColor(dna.getAliveBranchColorNight(), dna.getBudColorNight(), t, RGB);
    return PApplet.lerpColor(nightColor, dayColor, lightLevel, RGB);
  }

  //                  crack a cold one
  public int getFlowerBudLightColor(PlantDNA dna, int life) {
    float t = (float) life / Params.i(PName.BUD_TO_FLOWER_TIME);
    int dayColor = PApplet.lerpColor(dna.getAliveBranchColor(), dna.getBudColor(), t, RGB);
    return dayColor;
  }


  public int getSeedColor(PlantDNA dna, int age) {
    return getBranchColor(dna, age);
  }
}
