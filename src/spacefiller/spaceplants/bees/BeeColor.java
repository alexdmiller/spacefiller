package spacefiller.spaceplants.bees;

import processing.core.PApplet;
import spacefiller.Utils;

import static processing.core.PConstants.RGB;

public class BeeColor {
  private float lightLevel;
  private int hiveDayColor = 0xffffffff;
  private int hiveNightColor = 0xffffffff;
  private int wormDayColor = 0xffffffff;
  private int wormNightColor = 0xffffffff;

  public BeeColor(int hiveDayColor, int hiveNightColor, int wormDayColor, int wormNightColor) {
    this.hiveDayColor = hiveDayColor;
    this.hiveNightColor = hiveNightColor;
    this.wormDayColor = wormDayColor;
    this.wormNightColor = wormNightColor;
  }

  public void setLightLevel(float lightLevel) {
    this.lightLevel = lightLevel;
  }

  public int getHiveColor() {
    return PApplet.lerpColor(hiveNightColor, hiveDayColor, lightLevel, RGB);
  }

  public int getWormColor() {
    return PApplet.lerpColor(wormNightColor, wormDayColor, lightLevel, RGB);
  }
}
