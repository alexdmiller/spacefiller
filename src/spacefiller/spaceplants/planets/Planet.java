
package spacefiller.spaceplants.planets;

import processing.core.PGraphics;
import spacefiller.math.Vector;
import spacefiller.particles.ParticleTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class Planet {
  public static class Range {
    float inner;
    float outer;

    public Range(float inner, float outer) {
      this.inner = inner;
      this.outer = outer;
    }

    public float getInner() {
      return inner;
    }

    public float getOuter() {
      return outer;
    }

    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Range range = (Range) o;
      return Float.compare(range.inner, inner) == 0 &&
          Float.compare(range.outer, outer) == 0;
    }

    @Override public int hashCode() {
      return Objects.hash(inner, outer);
    }
  }

  // private Map<Range, Set<ParticleTag>> ranges;
  private Map<ParticleTag, Range> tagRanges;
  private Vector position;
  private float maxRadius;

  public Planet(Vector position) {
    tagRanges = new HashMap<>();
    this.position = position;
  }

  public void addRange(float inner, float outer, ParticleTag... tags) {
    Range range = new Range(inner, outer);

    for (ParticleTag tag : tags) {
      tagRanges.put(tag, range);
    }

    if (outer > maxRadius) {
      maxRadius = outer;
    }

//    if (!ranges.containsKey(range)) {
//      ranges.put(range, new HashSet<>());
//    }
//    ranges.get(range).addAll(Arrays.asList(tags));
  }

  public float getRadius() {
    return maxRadius;
  }

  public Range getRange(ParticleTag tag) {
    return tagRanges.get(tag);
  }

  public Vector getPosition() {
    return position;
  }

  public static int[] DEBUG_COLORS = new int[] {
      0xffff0000, 0xff00ff00, 0xff0000ff, 0xffffff00, 0xff00ffff, 0xffff00ff, 0xff330099
  };

  public void drawDebug(PGraphics canvas) {
    tagRanges.forEach((particleTag, range) -> {
      canvas.stroke(DEBUG_COLORS[particleTag.ordinal() % DEBUG_COLORS.length]);
      canvas.strokeWeight(1);
      canvas.noFill();
      canvas.ellipse(position.x,position.y, range.inner * 2, range.inner * 2);
      canvas.ellipse(position.x,position.y, range.outer * 2, range.outer * 2);
    });
  }

  public void setPosition(float x, float y) {
    this.position.set(x, y);
  }

}
